package id.attestation.service.email;

public class EmailData {
    private String recipient;
    private String subject;
    private String htmlBody;

    public EmailData(String recipient, String subject, String htmlBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.htmlBody = htmlBody;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }
}
