package br.com.livrementehomeopatia.backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.livrementehomeopatia.backend.model.Order;
import br.com.livrementehomeopatia.backend.model.HomeophaticOrder;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String subject, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            String html = buildHtml(link);
            helper.setText(html, true);

            var logo = new ClassPathResource("/static/images/logo.png");
            helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail", e);
        }
    }

    public void sendPaymentConfirmationEmail(String to, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Confirmação de Pagamento - Pedido #" + order.getId());

            String html = buildPaymentConfirmationHtml(order);
            helper.setText(html, true);

            var logo = new ClassPathResource("/static/images/logo.png");
            helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail de confirmação de pagamento", e);
        }
    }

    public void sendOrderNotificationToPharmacy(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("livrementehomeopatia@gmail.com");

            String orderType = order instanceof HomeophaticOrder ? "Homeopático" : "de Revenda";
            helper.setSubject("🛒 Novo Pedido " + orderType + " - #" + order.getId());

            String html = buildOrderNotificationHtml(order);
            helper.setText(html, true);

            var logo = new ClassPathResource("/static/images/logo.png");
            helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail de notificação de pedido", e);
        }
    }

    private String buildHtml(String link) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; text-align: center; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px;">
                    <img src='cid:logoImage' alt='Logo' style='height: 60px; margin-bottom: 20px;'/>
                    <h2 style="color: #333;">Recuperação de Senha</h2>
                    <p style="font-size: 16px; color: #555;">
                        Clique no botão abaixo para redefinir sua senha. O link expira em 5 minutos:
                    </p>
                    <a href='%s' style='
                        display: inline-block;
                        padding: 14px 28px;
                        margin-top: 20px;
                        background-color: #008400;
                        color: white;
                        font-weight: bold;
                        text-decoration: none;
                        border-radius: 8px;
                    '>Redefinir Senha</a>
                    <p style="margin-top: 30px; font-size: 14px; color: #888;">
                        Se você não solicitou a alteração, apenas ignore este e-mail.
                    </p>
                </div>
                <p style="font-size: 12px; color: #aaaaaa; margin-top: 30px;">© 2025 LivreMente Homeopatia</p>
            </body>
            </html>
            """.formatted(link);
    }

    private String buildPaymentConfirmationHtml(Order order) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; text-align: center; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px;">
                    <img src='cid:logoImage' alt='Logo' style='height: 60px; margin-bottom: 20px;'/>
                    <h2 style="color: #333;">Pagamento Confirmado!</h2>
                    <p style="font-size: 16px; color: #555;">
                        Olá %s,<br><br>
                        Seu pagamento do pedido <b>#%d</b> foi confirmado com sucesso.<br>
                        Valor total: <b>R$ %.2f</b>
                    </p>
                    <p style="margin-top: 30px; font-size: 14px; color: #888;">
                        Em breve seu pedido será processado e enviado.<br>
                        Obrigado por comprar na LivreMente Homeopatia!
                    </p>
                </div>
                <p style="font-size: 12px; color: #aaaaaa; margin-top: 30px;">© 2025 LivreMente Homeopatia</p>
            </body>
            </html>
            """.formatted(
                order.getUser().getFullName(),
                order.getId(),
                order.getTotalValue()
            );
    }

    private String buildOrderNotificationHtml(Order order) {
        String orderType = order instanceof HomeophaticOrder ? "Homeopático" : "Revenda";
        String orderIcon = order instanceof HomeophaticOrder ? "🌿" : "🏪";
        String actionNeeded;

        if (order instanceof HomeophaticOrder) {
            actionNeeded = "Aguardar o envio do orçamento e o contato via WhatsApp.";
        } else {
            actionNeeded = "Aguardar a confirmação do pagamento e entrar em contato com o cliente sobre a entrega.";
        }

        return """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;">
                <div style="max-width: 700px; margin: auto; background: white; padding: 30px; border-radius: 10px;">
                    <img src='cid:logoImage' alt='Logo' style='height: 60px; margin-bottom: 20px;'/>
                    <h2 style="color: #333;">%s Novo Pedido %s</h2>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 25px;">
                        <h3 style="margin: 0; color: #495057;">📋 Informações do Pedido</h3>
                        <p style="margin: 5px 0;"><b>Número:</b> #%d</p>
                        <p style="margin: 5px 0;"><b>Tipo:</b> %s</p>
                        <p style="margin: 5px 0;"><b>Data:</b> %s</p>
                        <p style="margin: 5px 0;"><b>Status:</b> %s</p>
                    </div>

                    <div style="background-color: #e3f2fd; padding: 15px; border-radius: 8px; margin-bottom: 25px;">
                        <h3 style="margin: 0; color: #1976d2;">👤 Dados do Cliente</h3>
                        <p style="margin: 5px 0;"><b>Nome:</b> %s</p>
                        <p style="margin: 5px 0;"><b>E-mail:</b> %s</p>
                        <p style="margin: 5px 0;"><b>Bairro:</b> %s</p>
                    </div>

                    <h3 style="color: #495057;">🛍️ Itens do Pedido</h3>
                    <table style="width:100%%; border-collapse: collapse; margin-bottom: 20px;">
                        <thead>
                            <tr style="background-color: #f8f9fa;">
                                <th style="border:1px solid #dee2e6; padding:12px; text-align:left;">Produto</th>
                                <th style="border:1px solid #dee2e6; padding:12px; text-align:center;">Qtd</th>
                                <th style="border:1px solid #dee2e6; padding:12px; text-align:right;">Valor Unit.</th>
                                <th style="border:1px solid #dee2e6; padding:12px; text-align:right;">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>

                    <div style="background-color: #e8f5e8; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                            <span><b>Subtotal:</b></span>
                            <span>R$ %.2f</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                            <span><b>Taxa de Entrega:</b></span>
                            <span>R$ %.2f</span>
                        </div>
                        <hr style="margin: 10px 0;">
                        <div style="display: flex; justify-content: space-between; font-size: 18px;">
                            <span><b>💰 Total:</b></span>
                            <span><b>R$ %.2f</b></span>
                        </div>
                    </div>

                    <div style="margin-top: 30px; padding: 15px; background-color: #fff3cd; border-radius: 8px;">
                        <p style="margin: 0; font-size: 14px; color: #856404;">
                            ⚠️ <b>Ação Necessária:</b> %s
                        </p>
                    </div>

                    <div style="margin-top: 20px; padding: 15px; background-color: #e9ecef; border-radius: 8px;">
                        <p style="margin: 0; font-size: 14px; color: #6c757d;">
                            📧 Este e-mail foi gerado automaticamente pelo sistema.<br>
                            🕐 Data/Hora: %s
                        </p>
                    </div>
                </div>
                <p style="font-size: 12px; color: #aaaaaa; margin-top: 30px; text-align: center;">© 2025 LivreMente Homeopatia</p>
            </body>
            </html>
            """.formatted(
                orderIcon, orderType,
                order.getId(),
                orderType,
                order.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                order.getStatus().name(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getNeighborhood() != null ? order.getNeighborhood().getName() : "N/A",
                buildOrderItemsTableRows(order),
                order.getTotalValue() - order.getDeliveryTax(),
                order.getDeliveryTax(),
                order.getTotalValue(),
                actionNeeded,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
    }

    private String buildOrderItemsTableRows(Order order) {
        StringBuilder sb = new StringBuilder();
        order.getItems().forEach(item -> {
            double subtotal = item.getPrice() * item.getQuantity();
            sb.append("<tr>")
                .append("<td style='border:1px solid #dee2e6; padding:10px;'>").append(item.getProduct().getName()).append("</td>")
                .append("<td style='border:1px solid #dee2e6; padding:10px; text-align:center;'>").append(item.getQuantity()).append("</td>")
                .append("<td style='border:1px solid #dee2e6; padding:10px; text-align:right;'>R$ ").append(String.format("%.2f", item.getPrice())).append("</td>")
                .append("<td style='border:1px solid #dee2e6; padding:10px; text-align:right;'>R$ ").append(String.format("%.2f", subtotal)).append("</td>")
                .append("</tr>");
        });
        return sb.toString();
    }
}