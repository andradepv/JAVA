package servidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12348;
    private static HashMap<String, User> users = new HashMap<>();
    private static HashMap<String, PrintWriter> clientWriters = new HashMap<>();
    private static HashMap<String, List<String>> offlineMessages = new HashMap<>();
    private static HashMap<String, ClientHandler> clientHandlers = new HashMap<>();

    public static void main(String[] args) {
    	loadUsers();
    	loadOfflineMessages();
        users.put("prof1", new Professor("prof1", "password1", "Doutor"));
        users.put("alu1", new Aluno("aluno1", "password2", 2023));
        users.put("tec1", new Tecnico("tec1", "123"));
        Scanner scanner = new Scanner(System.in);

        if (!authenticateTechnician(scanner)) {
            System.out.println("Autenticação falhou. Encerrando o servidor.");
            return;
        }

        new Thread(ChatServer::startServer).start();

        while (true) {
            System.out.println("1. Cadastro de Professor");
            System.out.println("2. Cadastro de Aluno");
            System.out.println("3. Cadastro de Técnico");
            System.out.println("4. Listar Usuários");
            System.out.println("5. Listar Usuários online");
            System.out.println("6. Fechar conexão de usuário");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    registerProfessor(scanner);
                    break;
                case 2:
                    registerAluno(scanner);
                    break;
                case 3:
                    registerTecnico(scanner);
                    break;
                case 4:
                    listUser();
                    break;
                case 5:
                    listUsers();
                    break;
                case 6:
                    closeConnection(scanner);
                    break;
                case 7:
                    System.out.println("Encerrando servidor...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private static boolean authenticateTechnician(Scanner scanner) {
        System.out.println("Autenticação de Técnico:");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        if (users.containsKey(login) && users.get(login) instanceof Tecnico && users.get(login).getPassword().equals(password)) {
            System.out.println("Autenticação bem-sucedida como técnico.");
            return true;
        } else {
            System.out.println("Autenticação falhou. Usuário não é um técnico válido.");
            return false;
        }
    }

    private static void registerProfessor(Scanner scanner) {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        System.out.print("Titulação: ");
        String titulacao = scanner.nextLine();
        registerUser(new Professor(login, password, titulacao));
    }

    private static void registerAluno(Scanner scanner) {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        System.out.print("Ano de Ingresso: ");
        int anoIngresso = scanner.nextInt();
        scanner.nextLine();
        registerUser(new Aluno(login, password, anoIngresso));
    }

    private static void registerTecnico(Scanner scanner) {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        registerUser(new Tecnico(login, password));
    }

    private static void listUser() {
        for (User user : users.values()) {
            if (user instanceof Professor) {
                Professor professor = (Professor) user;
                System.out.println("Login: " + professor.getLogin() + ", Senha: " + professor.getPassword() + ", Titulação: " + professor.getTitulacao());
            } else if (user instanceof Aluno) {
                Aluno aluno = (Aluno) user;
                System.out.println("Login: " + aluno.getLogin() + ", Senha: " + aluno.getPassword() + ", Ano de Ingresso: " + aluno.getAnoIngresso());
            } else if (user instanceof Tecnico) {
                Tecnico tecnico = (Tecnico) user;
                System.out.println("Login: " + tecnico.getLogin() + ", Senha: " + tecnico.getPassword());
            }
        }
    }

    private static void listUsers() {
        synchronized (users) {
            for (User user : users.values()) {
                String status = user.isOnline() ? "Online" : "Offline";
                System.out.println(user.getLogin() + " (" + status + ")" + (user.getUnreadMessages().isEmpty() ? "" : " *"));
            }
        }
    }

    private static void closeConnection(Scanner scanner) {
        System.out.print("Login do usuário a desconectar: ");
        String login = scanner.nextLine();
        killUserConnection(login);
    }

    private static void startServer() {
        System.out.println("Iniciando o chat...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private User user;
        private BufferedReader in;
        private PrintWriter out;
        private String mode = "global";

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("LOGIN");
                    String login = in.readLine();
                    String password = in.readLine();

                    if (users.containsKey(login) && users.get(login).getPassword().equals(password)) {
                        user = users.get(login);
                        user.setOnline(true);
                        out.println("SUCCESS");
                        synchronized (clientWriters) {
                            clientWriters.put(login, out);
                            clientHandlers.put(login, this);
                        }
                        sendOfflineMessages(login);
                        break;
                    } else {
                        out.println("FAIL");
                    }
                }

                out.println("Bem-vindo ao chat, " + user.getLogin() + "!");
                broadcast(user.getLogin() + " entrou no chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/")) {
                        handleClientCommands(this, message.substring(1));
                    } else {
                        handleMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (user != null) {
                    user.setOnline(false);
                    synchronized (clientWriters) {
                        clientWriters.remove(user.getLogin());
                        clientHandlers.remove(user.getLogin());
                    }
                    broadcast(user.getLogin() + " saiu do chat.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveUsers();
                saveOfflineMessages();
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters.values()) {
                    writer.println(message);
                }
                for (User u : users.values()) {
                    if (!u.isOnline()) {
                        offlineMessages.computeIfAbsent(u.getLogin(), k -> new ArrayList<>()).add(message);
                    }
                }
            }
        }

        private void sendOfflineMessages(String login) {
            List<String> messages = offlineMessages.getOrDefault(login, new ArrayList<>());
            for (String message : messages) {
                out.println(message);
            }
            offlineMessages.remove(login);
        }

        private void handleMessage(String message) {
            if (mode.equals("global")) {
                broadcast(user.getLogin() + ": " + message);
            } else if (mode.equals("private")) {

                String[] parts = message.split(" ", 2);
                if (parts.length < 2) {
                    out.println("Formato de mensagem privada inválido.");
                    return;
                }
                String recipient = parts[0];
                String privateMessage = parts[1];
                if (users.containsKey(recipient)) {
                    if (users.get(recipient).isOnline()) {
                        PrintWriter recipientWriter = clientWriters.get(recipient);
                        if (recipientWriter != null) {
                            recipientWriter.println(user.getLogin() + " (privado): " + privateMessage);
                        }
                    } else {
                        offlineMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add(user.getLogin() + " (privado): " + privateMessage);
                    }
                } else {
                    out.println("Usuário não encontrado.");
                }
            }
        }

        private void handleClientCommands(ClientHandler clientHandler, String command) {
            switch (command) {
                case "1":
                    clientHandler.mode = "global";
                    out.println("Modo alterado para chat global.");
                    break;
                case "2": 
                    clientHandler.mode = "private";
                    out.println("Modo alterado para mensagens privadas.");
                    break;
                case "3":
                    listUsersForClient(out);
                    break;
                case "4":
                    clientHandler.logout();
                    break;
                default:
                    out.println("Opção inválida.");
                    break;
            }
        }

        private void listUsersForClient(PrintWriter out) {
            synchronized (users) {
                for (User user : users.values()) {
                    String status = user.isOnline() ? "Online" : "Offline";
                    out.println(user.getLogin() + " (" + status + ")" + (user.getUnreadMessages().isEmpty() ? "" : " *"));
                }
            }
            out.println("FIM");
        }


        private void logout() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public PrintWriter getOut() {
            return out;
        }
    }

    public static void registerUser(User user) {
        users.put(user.getLogin(), user);
        saveUsers();
    }

    public static void killUserConnection(String login) {
        if (users.containsKey(login)) {
            users.get(login).setOnline(false);
            synchronized (clientWriters) {
                PrintWriter writer = clientWriters.remove(login);
                if (writer != null) {
                    writer.println("DESCONECTADO");
                }
                ClientHandler handler = clientHandlers.remove(login);
                if (handler != null) {
                    try {
                        handler.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            users = (HashMap<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing user data found.");
        }
    }

    private static void saveOfflineMessages() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("offlineMessages.dat"))) {
            oos.writeObject(offlineMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadOfflineMessages() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("offlineMessages.dat"))) {
            offlineMessages = (HashMap<String, List<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing offline messages data found.");
        }
    }
}