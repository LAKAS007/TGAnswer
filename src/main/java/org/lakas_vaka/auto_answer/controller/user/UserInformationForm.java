package org.lakas_vaka.auto_answer.controller.user;

import lombok.Data;

@Data
public class UserInformationForm {
    private String login;
    private String name;
    private String gender;
    private String type;
    private String neuralModel;
}
