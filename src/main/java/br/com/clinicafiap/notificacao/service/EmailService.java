package br.com.clinicafiap.notificacao.service;

import br.com.clinicafiap.notificacao.record.EmailRecord;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String remetente;

	@KafkaListener(topicPartitions = @TopicPartition(
			topic = "notificacao-email",
			partitions = { "1" }),
			containerFactory = "emailKafkaListenerContainerFactory")
	public void emailListener(EmailRecord email) {
		var retorno = enviarEmailTexto(email.destinatario(), email.assunto(), email.mensagem());
		System.out.println(retorno);
	}
	
	public String enviarEmailTexto(String destinatario, String assunto, String mensagem) {
		
//		try {
//			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//			simpleMailMessage.setFrom(remetente);
//			simpleMailMessage.setTo(destinatario);
//			simpleMailMessage.setSubject(assunto);
//			simpleMailMessage.setText(mensagem);
//			javaMailSender.send(simpleMailMessage);
//			return "Email enviado";
//		}catch(Exception e) {
//			return "Erro ao tentar enviar email " + e.getLocalizedMessage();
//		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("posfiapteam35@gmail.com", "jbvp ugel gull ksqr");
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("posfiapteam35@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
			message.setSubject(assunto);
			message.setText(mensagem);

			Transport.send(message);

			return "Email enviado";
		} catch (MessagingException e) {
			return "Erro ao tentar enviar email " + e.getLocalizedMessage();
		}
	}
}
