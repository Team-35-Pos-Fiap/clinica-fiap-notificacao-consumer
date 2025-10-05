package br.com.clinicafiap.notificacao.service.interfaces;

import br.com.clinicafiap.notificacao.record.EmailRecord;
import jakarta.mail.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IEmailService {
	String enviarEmailTexto(String destinatario, String assunto, String mensagem);

	Session configuraemail();

	void emailListener(EmailRecord email);
}