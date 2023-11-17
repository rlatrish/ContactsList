package com.example.ContactsList.service;

import com.example.ContactsList.dto.Contact;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

@Component
public class ServiceForContacts {
    private final String PATTERNS_NAME = "[\\D\\s]+";
    private final String PATTERNS_PHONE
            = "^((\\+\\d{1,3}( )?)?|8)((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^((\\+\\d{1,3}( )?)?|8)(\\d{3}[ ]?){2}\\d{3}$"
            + "|^((\\+\\d{1,3}( )?)?|8)(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    private final String PATTENS_EMAIL = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}";
    private final String ERROR_MSG = " Ошибка ввода: ";
    private final String ERROR_MSG_END = " не корректно - ";

    @Value("${contactsList.fileName.in}")
    private String inFileName;

    @Value("${contactsList.fileName.out}")
    private String outFileName;

    @Value("${spring.profile.active}")
    private String profile;

    private Map<String, Contact> map = new TreeMap<>();

    @PostConstruct
    public void ServiceForContactsPost() {
        if (profile.equalsIgnoreCase("init")) {
            System.out.println("Профль приложения = " + profile);
            System.out.println("Инициализация хранилища через файл " + inFileName);
            readContacts();
            System.out.println("Приложение готово к работе" + System.lineSeparator());
        }
    }

    public void printContacts() {
        map.forEach((s,v)->System.out.println(v));
    }
    private void printExample() {
        System.out.println("  Пример ввода:  Иванов Иван Иванович; +890999999; someEmail@example.example");
    }

    private void customError(boolean flag, String msg, String value) {
        if (!flag) {
            System.out.println(ERROR_MSG + msg + ERROR_MSG_END + " " + value);
        }
    }
    private void checkContactString(String line) {
        if (line == null || line.length() < 7) {
            System.out.println(ERROR_MSG + " данных не достаточно");
            return;
        }
        String [] element = line.split(";");
        if (element.length != 3) {
            System.out.println(ERROR_MSG + "должно быть три значения, разделенных знаком \";\"");
            printExample();
            System.out.println("В строке \"" + line + "\"");
            System.out.println(element.length + " значений");
            for (String s : element) {
                System.out.println("\"" + s + "\"");
            }
            return;
        }
        for (int i = 0; i < 3; i++) {
            element[i] = element[i].trim();
        }
        boolean nameFlag = element[0].matches(PATTERNS_NAME);
        boolean numberFlag = element[1].matches(PATTERNS_PHONE);
        boolean emailFlag = element[2].matches(PATTENS_EMAIL);
        if (nameFlag && numberFlag && emailFlag) {
            Contact old = map.put(element[2], new Contact(element[0], element[1], element[2]));
            if (old == null) {
                System.out.println("Контакт добавлен.");
            } else {
                System.out.println("Контакт заменен: " + old);
            }
        }
        customError(nameFlag,"ФИО введено", element[0]);
        customError(numberFlag,"номер телефона введен", element[1]);
        customError(emailFlag,"электронный адрес введен", element[2]);
    }
    public void addContact() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите данные нового контакта в формате: ФИО; номер телефона; электронная почта");
        printExample();
        checkContactString(scanner.nextLine());
    }
    public void deleteContact() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите электронный адрес для удаления контакта (контакт с этим адресом будет удален)");
        String email = scanner.nextLine().trim().toLowerCase();
        if (email.matches(PATTENS_EMAIL)) {
            Contact contact = map.remove(email);
            System.out.println("Контакт с электронным адресом " + email + " " +
                    (contact == null ? "не найден" : "удален"));
        } else {
            System.out.println(email + " не может являться электроным адресом");
        }
    }
    public void saveContacts() {
        try {
            FileWriter writer = new FileWriter(outFileName);
            map.forEach((email, contact) -> {
                try {
                    writer.write(contact.toString().replace(" | ",";") + System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.close();
        } catch (IOException ex) {
            System.out.println("Не получилось записать список контактов в файл " + outFileName);
            ex.printStackTrace();
            return;
        }
        System.out.println("Список контактов записан в файл " + outFileName);
    }

    private void readContacts() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(inFileName));
            lines.forEach(this::checkContactString);
        } catch (IOException ex) {
            System.out.println("Не могу прочитать для инициализации данных файл " + inFileName);
            ex.printStackTrace();
        }
    }
}
