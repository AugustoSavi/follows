package com.users.follows.service;

import com.errorxcode.jxinsta.InstagramException;
import com.errorxcode.jxinsta.JxInsta;
import com.errorxcode.jxinsta.Utils;
import com.errorxcode.jxinsta.endpoints.profile.Profile;
import com.users.follows.model.Follow;
import com.users.follows.model.FollowAlert;
import com.users.follows.model.FollowsId;
import com.users.follows.model.Username;
import com.users.follows.repository.FollowAlertRepository;
import com.users.follows.repository.FollowRepository;
import com.users.follows.repository.UsernameRepository;
import com.users.follows.representation.AddUsernameRepresentation;
import com.users.follows.representation.UserRepresentation;
import com.users.follows.util.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import port.org.json.JSONArray;
import port.org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class FollowsService {
    public static int NUMERO_DE_SEGUIDORES_A_BUSCAR = 12;
    private final JxInsta instagram;
    private final UsernameRepository usernameRepository;
    private final FollowRepository followRepository;
    private final FollowAlertRepository followAlertRepository;
    private final JsonConverter jsonConverter = new JsonConverter();
    private final PublishAlertService publishAlertService;


    public FollowsService(JxInsta instagram, UsernameRepository usernameRepository, FollowRepository followRepository, FollowAlertRepository followAlertRepository, PublishAlertService publishAlertService) {
        this.instagram = instagram;
        this.usernameRepository = usernameRepository;
        this.followRepository = followRepository;
        this.followAlertRepository = followAlertRepository;
        this.publishAlertService = publishAlertService;
    }

    @Async
    public void addNewUser(AddUsernameRepresentation username) {
        try {
            Profile profile = instagram.getProfile(username.username());
            int followingsCount = profile.followings;
            int interacoes = (followingsCount / NUMERO_DE_SEGUIDORES_A_BUSCAR) + (followingsCount % NUMERO_DE_SEGUIDORES_A_BUSCAR > 0 ? 1 : 0);

            if (followingsCount == 0) {
                log.info("User {} has no followings to add", username.username());
                return;
            }

            usernameRepository.save(new Username(profile.pk, profile.username, username.key_imagem_publicacao()));

            log.info("Profile {} segue {}, sera realizado: {} interações", profile.full_name, profile.followings, interacoes);
            for (int i = 1; i <= interacoes; i++) {
                log.info("Buscando seguidores do usuário {}: Interação {} de {}", username.username(), i, interacoes);

                List<UserRepresentation> followings = getFollowings(profile.pk, instagram, NUMERO_DE_SEGUIDORES_A_BUSCAR * i);
                if (followings.isEmpty()) {
                    log.info("buscamos todas as pessoas que {} segue!", username);
                    break;
                }
                for (UserRepresentation user : followings) {
                    addNewFollowUser(profile.pk, profile.username, user.pk(), user.username());
                }
                try {
                    int sleepTime = 20000 + (int) (Math.random() * 20000);
                    log.info("Sleeping for {} milliseconds to avoid rate limiting", sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.error("Thread interrupted while sleeping: {}", e.getMessage());
                }
            }
            log.info("adicão das pessoas que o {} segue finalizada com sucesso!", username);
        } catch (InstagramException | IOException e) {
            log.error("Erro ao adicionar as pessoas que o user {} segue: {}", username, e.getMessage());
        }
    }


    @Async
    public void updateNewFollowings(String username) {
        try {
            Profile profile = instagram.getProfile(username);
            int followingsCount = profile.followings;
            int interacoes = (followingsCount / NUMERO_DE_SEGUIDORES_A_BUSCAR) + (followingsCount % NUMERO_DE_SEGUIDORES_A_BUSCAR > 0 ? 1 : 0);

            log.info("Profile {} segue {}, sera realizado: {}", profile.full_name, profile.followings, interacoes);
            for (int i = 1; i <= interacoes; i++) {
                List<UserRepresentation> followings = getFollowings(profile.pk, instagram, NUMERO_DE_SEGUIDORES_A_BUSCAR * i);
                if (followings.isEmpty()) {
                    log.info("Nao existem mais seguidores para serem buscados");
                    break;
                }
                for (UserRepresentation user : followings) {
                    boolean newFollowUser = addNewFollowUser(profile.pk, profile.username, user.pk(), user.username());
                    if (newFollowUser) {
                        publishAlertService.publishAlert(profile.username, user.username());
                    }
                }
            }
            log.info("Busca de seguidores finalizada com sucesso!");
        } catch (InstagramException | IOException e) {
            log.error(e.getMessage());
        }
    }

    public List<UserRepresentation> getFollowings(Long pk, JxInsta authInfo, int maxId) throws IOException, InstagramException {
        String url = "friendships/" + pk + "/following/?count=" + NUMERO_DE_SEGUIDORES_A_BUSCAR;
        if (maxId > NUMERO_DE_SEGUIDORES_A_BUSCAR) {
            url += "&max_id=" + maxId;
        }
        log.info("Fetching followings from URL: {}", url);
        Request req = Utils.createGetRequest(url, authInfo);

        try (Response response = Utils.call(req, authInfo)) {
            JSONObject json = new JSONObject(response.body().string());
            ArrayList<UserRepresentation> list = new ArrayList<>();
            JSONArray users = json.getJSONArray("users");

            for (int i = 0; i < users.length(); ++i) {
                JSONObject userJsonObject = users.getJSONObject(i);
                UserRepresentation convert = jsonConverter.convert(userJsonObject, UserRepresentation.class);
                list.add(convert);
            }

            return list;
        } catch (IOException e) {
            log.error("Erro ao buscar seguidores: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean addNewFollowUser(Long usernameId, String username, Long followsId, String followsUsername) {
        FollowsId followId = new FollowsId(usernameId, followsId);
        boolean following = isFollowing(followId);
        if (!following) {
            followRepository.save(new Follow(usernameId, username, followsId, followsUsername));
            log.info("User {} started following {}", username, followsUsername);
            String message = username + " começou a seguir " + followsUsername;
            FollowAlert followAlert = new FollowAlert(username, followsUsername, message);
            followAlertRepository.save(followAlert);
            return true;
        }
        log.info("User {} already follows {}", username, followsUsername);
        return false;
    }

    private boolean isFollowing(FollowsId followId) {
        return followRepository.existsById(followId);
    }
}
