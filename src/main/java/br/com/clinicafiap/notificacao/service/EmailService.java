package br.com.clinicafiap.notificacao.service;

import br.com.clinicafiap.notificacao.record.EmailRecord;
import br.com.clinicafiap.notificacao.service.interfaces.IEmailService;
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
public class EmailService implements IEmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${app.email.remetente}")
	private String remetente;

	@Value("${app.email.senha}")
	private String senha;

	@KafkaListener(topicPartitions = @TopicPartition(
			topic = "notificacao-email",
			partitions = { "1" }),
			containerFactory = "emailKafkaListenerContainerFactory")
	public void emailListener(EmailRecord email) {
		var retorno = enviarEmailTexto(email.destinatario(), email.assunto(), email.mensagem());
		System.out.println(retorno);
	}

	public Session configuraemail(){
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });
	}
	
	public String enviarEmailTexto(String destinatario, String assunto, String mensagem) {

		Session session = configuraemail();

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(remetente));
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
