package com.demo.external.email;

import lombok.Data;

@Data
public class Mail {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String template;
    private Object templateVariables;
}
