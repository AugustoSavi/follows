package com.users.follows.service;

import com.errorxcode.jxinsta.JxInsta;
import com.users.follows.model.Username;
import com.users.follows.repository.UsernameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class PublishAlertService {

    private final UsernameRepository usernameRepository;
    private final JxInsta instagram;
    private final S3Service s3Service;

    public PublishAlertService(UsernameRepository usernameRepository, JxInsta instagram, S3Service s3Service) {
        this.usernameRepository = usernameRepository;
        this.instagram = instagram;
        this.s3Service = s3Service;
    }

    @Async
    public void publishAlert(String username, String followingUsername) {
        try {
            Username usernameOrigem = usernameRepository.getUsernamesByUsername(username);
            if (usernameOrigem == null) {
                log.warn("Username {} does not exist in the database", username);
                return;
            }
            Username usernameDestino = usernameRepository.getUsernamesByUsername(followingUsername);
            if (usernameDestino == null) {
                log.warn("Following username {} does not exist in the database", followingUsername);
                return;
            }

            String alertMessage = String.format("\uD83D\uDD75\uFE0F \uD83D\uDEA8 Alerta: %s começou a seguir %s no instagram. Profile picture: %s, Following profile picture: %s",
                    usernameOrigem.getUsername(), usernameDestino.getUsername(), usernameOrigem.getUrl_imagem_publicacao(), usernameDestino.getUrl_imagem_publicacao());

            byte[] imagemUsernameOrigem = s3Service.download(usernameOrigem.getUrl_imagem_publicacao());
            byte[] imagemUsernameDestino = s3Service.download(usernameDestino.getUrl_imagem_publicacao());
            InputStream imagem = unirImagensLadoALado(imagemUsernameOrigem, imagemUsernameDestino);

            instagram.postPicture(imagem, alertMessage, false);


            log.info("New follow alert: {}", alertMessage);

        } catch (Exception e) {
            log.error("error buscando profiles: {}", e.getMessage());
        }
    }

    public InputStream unirImagensLadoALado(byte[] imgBytes1, byte[] imgBytes2) throws IOException {
        // 1. Converter bytes para BufferedImage
        BufferedImage img1 = ImageIO.read(new ByteArrayInputStream(imgBytes1));
        BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(imgBytes2));

        // 2. Calcular dimensões da nova imagem
        int larguraTotal = img1.getWidth() + img2.getWidth();
        int alturaMaxima = Math.max(img1.getHeight(), img2.getHeight());

        // 3. Criar nova imagem
        BufferedImage imagemFinal = new BufferedImage(larguraTotal, alturaMaxima, BufferedImage.TYPE_INT_RGB);
        Graphics g = imagemFinal.getGraphics();

        // 4. Desenhar as imagens
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);
        g.dispose();

        // 5. Converter BufferedImage final para byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagemFinal, "jpg", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
