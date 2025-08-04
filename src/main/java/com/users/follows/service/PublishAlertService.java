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
import java.io.*;

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

            String alertMessage = String.format("\uD83D\uDD75\uFE0F \uD83D\uDEA8 Alerta: @%s começou a seguir @%s no instagram.", usernameOrigem.getUsername(), usernameDestino.getUsername());

            byte[] imagemUsernameOrigem = redimensionarImagem(s3Service.download(usernameOrigem.getKey_imagem()));
            byte[] imagemUsernameDestino = redimensionarImagem(s3Service.download(usernameDestino.getKey_imagem()));
            InputStream imagem = unirImagensLadoALado(imagemUsernameOrigem, imagemUsernameDestino);

            instagram.postPicture(imagem, alertMessage, false);

//            salvarImagemNaRaiz(imagem, String.format("%s_x_%s.jpg", username, followingUsername));

            log.info("New follow alert: {}", alertMessage);

        } catch (Exception e) {
            log.error("error buscando profiles: {}", e.getMessage());
        }
    }

    public InputStream unirImagensLadoALado(byte[] imgBytes1, byte[] imgBytes2) throws IOException {
        BufferedImage img1 = ImageIO.read(new ByteArrayInputStream(imgBytes1));
        log.info("Dimensões da imagem 1: {}x{}", img1.getWidth(), img1.getHeight());

        BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(imgBytes2));
        log.info("Dimensões da imagem 2: {}x{}", img2.getWidth(), img2.getHeight());

        int larguraTotal = img1.getWidth() + img2.getWidth();
        int alturaMaxima = Math.max(img1.getHeight(), img2.getHeight());

        BufferedImage imagemFinal = new BufferedImage(larguraTotal, alturaMaxima, BufferedImage.TYPE_INT_RGB);
        Graphics g = imagemFinal.getGraphics();

        g.setColor(Color.WHITE);

        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);
        g.dispose();

        log.info("Dimensões da imagem final: {}x{}", imagemFinal.getWidth(), imagemFinal.getHeight());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagemFinal, "jpg", baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void salvarImagemNaRaiz(InputStream imagem, String nomeArquivo) {
        try {
            BufferedImage imagemBuffer = ImageIO.read(imagem);
            if (imagemBuffer == null) {
                throw new IOException("Não foi possível ler a imagem do InputStream.");
            }

            File arquivoSaida = new File(nomeArquivo);
            ImageIO.write(imagemBuffer, "jpg", arquivoSaida);

            System.out.println("Imagem salva em: " + arquivoSaida.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a imagem localmente: " + e.getMessage(), e);
        }
    }

    private byte[] redimensionarImagem(byte[] imagem) throws IOException {
        int largura = 540;
        int altura = 566;

        BufferedImage imgOriginal = ImageIO.read(new ByteArrayInputStream(imagem));
        if (imgOriginal == null) {
            throw new IOException("Não foi possível ler a imagem do InputStream.");
        }

        if (imgOriginal.getWidth() == largura && imgOriginal.getHeight() == altura) {
            return imagem;
        }

        BufferedImage imgRedimensionada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imgRedimensionada.createGraphics();
        g.drawImage(imgOriginal, 0, 0, largura, altura, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imgRedimensionada, "jpg", baos);
        return baos.toByteArray();
    }

}
