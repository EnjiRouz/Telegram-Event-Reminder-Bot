package main.serivce;

import main.entity.Form;
import org.springframework.stereotype.Service;
import main.repository.FormsRepository;

@Service
public class FormService {
    private final FormsRepository formsRepository;


    public FormService(FormsRepository formsRepository) {
        this.formsRepository = formsRepository;
    }

    public void createForm(Form form) {
        formsRepository.save(form);
    }
}
