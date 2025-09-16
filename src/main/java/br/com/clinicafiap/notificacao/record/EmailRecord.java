package br.com.clinicafiap.notificacao.record;

import java.math.BigDecimal;

public record EmailRecord(Long id, String destinatario, String assunto, String mensagem) {
    
}
