package todo;

import todo.manager.ToDoManager;
import todo.manager.UserManager;
import todo.model.ToDo;
import todo.model.ToDoStatus;
import todo.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ToDoMain implements Commands {

    private static Scanner scanner = new Scanner(System.in);
    public static UserManager userManager = new UserManager();
    public static ToDoManager toDoManager = new ToDoManager();
    private static User currentUser = null;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        boolean isRun = true;
        while (isRun) {
            Commands.printMainCommands();
            int command;
            try {
                command = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                command = -1;
            }
            switch (command) {
                case EXIT:
                    isRun = false;
                    break;
                case LOGIN:
                    loginUser();
                    break;
                case REGISTER:
                    registerUser();
                    break;
                default:
                    System.err.println("Wrong command !!");
            }
        }

    }

    private static void registerUser() {
        System.out.println(" please input user data (name, surname, email, password)");
        try {
            String userDataStr = scanner.nextLine();
            String[] userDataArr = userDataStr.split(",");
            User userFromStorage = userManager.getByEmail(userDataArr[2]);
            if (userFromStorage == null) {
                User user = new User();
                user.setName(userDataArr[0]);
                user.setSurname(userDataArr[1]);
                user.setEmail(userDataArr[2]);
                user.setPassword(userDataArr[3]);
                if (userManager.register(user)) {
                    System.out.println("User was successfully added");
                } else {
                    System.err.println("Something went wrong!");
                }
            } else {
                System.err.println("User already exists! ");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Wrong Data!");
        }
    }

    private static void loginUser() {
        System.out.println(" please input email, password");
        try {
            String loginStr = scanner.nextLine();
            String[] loginArr = loginStr.split(",");
            User user = userManager.getByEmailAndPassword(loginArr[0], loginArr[1]);
            if (user != null) {
                currentUser = user;
                loginSuccess();
            } else {
                System.err.println("Wrong email or password");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Wrong Data");
        }

    }

    private static void loginSuccess() {
        System.out.println("Welcome " + currentUser.getName() + " !");
        boolean isRun = true;
        while (isRun) {
            Commands.printUserCommands();
            int command;
            try {
                command = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                command = -1;
            }
            switch (command) {
                case LOGOUT:
                    isRun = false;
                    break;
                case ADD_NEW_TODO:
                    addNewToDo();
                    break;
                case MY_ALL_LIST:
                    printToDod(toDoManager.getAllToDosByUser(currentUser.getId()));
                    break;
                case MY_TODO_LIST:
                    printToDod(toDoManager.getAllToDosByUserAndStatus(currentUser.getId(), ToDoStatus.TODO));
                    break;
                case MY_IN_PROGRESS_LIST:
                    printToDod(toDoManager.getAllToDosByUserAndStatus(currentUser.getId(), ToDoStatus.IN_PROGRESS));
                    break;
                case MY_FINESHED_LIST:
                    printToDod(toDoManager.getAllToDosByUserAndStatus(currentUser.getId(), ToDoStatus.FINISHED));
                    break;
                case CHANGE_TODO_STATUS:
                    changeToDoStatus();
                    break;
                case DELETE_TODO:
                    deleteToDo();
                    break;
                default:
                    System.err.println("Wrong command");

            }

        }

    }

    private static void printToDod(List<ToDo> allToDosByUser) {
        for (ToDo toDo : allToDosByUser) {
            System.out.println(toDo);
        }
    }

    private static void addNewToDo() {
        System.out.println("Please input title,deadline (yyyy-MM-dd HH:mm:ss)");
        String toDoDataStr = scanner.nextLine();
        String[] split = toDoDataStr.split(",");
        ToDo toDo = new ToDo();
        try {
            String title = split[0];
            toDo.setTitle(title);
            try {
                if (split[1] != null) {
                    toDo.setDeadline(sdf.parse(split[1]));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            } catch (ParseException e) {
                System.err.println("Please input date by this format: yyyy-MM-dd HH:mm:ss ");
            }
            toDo.setStatus(ToDoStatus.TODO);
            toDo.setUser(currentUser);
            if (toDoManager.create(toDo)) {
                System.out.println("ToDo was added");
            } else {
                System.err.println("Something went wrong !");
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Wrong data");
        }
    }


    private static void deleteToDo() {
        System.out.println("Please choose TODO from list:");
        List<ToDo> list = toDoManager.getAllToDosByUser(currentUser.getId());
        for (ToDo toDo : list) {
            System.out.println(toDo);
        }
        long id = Long.parseLong(scanner.nextLine());
        ToDo byId = toDoManager.getById(id);
        if (byId.getUser().getId() == currentUser.getId()) {
            toDoManager.delete(id);
        } else {
            System.err.println("Wrong ID");
        }
    }

    private static void changeToDoStatus() {
        System.out.println("Please choose TODO from list:");
        List<ToDo> list = toDoManager.getAllToDosByUser(currentUser.getId());
        for (ToDo toDo : list) {
            System.out.println(toDo);
        }
        long id = Long.parseLong(scanner.nextLine());
        ToDo byId = toDoManager.getById(id);
        if (byId.getUser().getId() == currentUser.getId()) {
            System.out.println("Please choose Status");
            System.out.println(Arrays.toString(ToDoStatus.values()));
            ToDoStatus status = ToDoStatus.valueOf(scanner.nextLine());
            if (toDoManager.update(id, status)) {
                System.out.println("Status was changed");
            } else {
                System.out.println("Something went wrong !");
            }
        } else {
            System.err.println("Wrong ID");
        }

    }

}
