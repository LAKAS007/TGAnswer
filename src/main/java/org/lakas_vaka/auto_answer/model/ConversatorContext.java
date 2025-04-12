package org.lakas_vaka.auto_answer.model;

import lombok.*;
import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.neural.service.NeuralModel;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ConversatorContext {
    public static final ConversatorContext EMPTY = new ConversatorContext(Gender.MALE, "", List.of(),
            ConversatorType.UNDEFINED, null);

    private Gender conversatorGender;
    private String conversatorName;
    @NonNull
    private List<String> additionalInformation;
    private ConversatorType conversatorType;
    private NeuralModel neuralModel;

    public boolean isEmpty() {
        return this == EMPTY ||
                (conversatorName.isEmpty() && conversatorGender.equals(
                        Gender.MALE) && additionalInformation.isEmpty() && conversatorType.equals(
                        ConversatorType.UNDEFINED));
    }

    public void addInformation(String information) {
        additionalInformation.add(information);
    }
}
