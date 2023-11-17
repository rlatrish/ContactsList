package com.example.ContactsList;

import com.example.ContactsList.service.ServiceForContacts;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class ContactsListApplication {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		System.out.println("Небольшое консольное приложение \"Контакты\"");
		Scanner scanner = new Scanner(System.in);
		String choice = "*";
		ServiceForContacts service = context.getBean(ServiceForContacts.class);
		while (!choice.isEmpty()) {
			System.out.println("--------------------------------------------------");
			System.out.println("1. Вывести весь список контактов.");
			System.out.println("2. Добавить новый контакт в список.");
			System.out.println("3. Удалить контакт по e-mail.");
			System.out.println("4. Сохранить список контактов в файл.");
			System.out.println("0. Закончить работу программы.");
			System.out.print("  Что вы хотите сделать (введите число от 0 до 4): ");
			choice = scanner.next();
			System.out.println(" Выбрано : " + choice);
			switch (choice) {
				case "1": service.printContacts(); break;
				case "2": service.addContact(); break;
				case "3": service.deleteContact(); break;
				case "4": service.saveContacts(); break;
				case "0": choice = ""; break;
				default:
					System.out.println(" Ошибка ввода при выборе действия. Попробуйте ещё раз.");
			}
		}
		System.out.println(" Программа успешно завершена.");
	}
}
