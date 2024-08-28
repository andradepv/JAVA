package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12348;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Bem-Vindo ao PVAF: ");
                System.out.print("Privado Virtual Amigos Falantes \n");
                System.out.print("Usuário: ");
                String username = scanner.nextLine();
                System.out.print("Senha: ");
                String password = scanner.nextLine();

                out.println(username);
                out.println(password);

                String response = in.readLine();
                if ("SUCCESS".equals(response)) {
                    System.out.println("Login bem-sucedido!");
                    break;
                } else {
                    System.out.println("Falha no login. Tente novamente.");
                }
            }


            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        if (message.equals("LIST_USERS")) {

                            while ((message = in.readLine()) != null && !message.equals("END_OF_LIST")) {
                                System.out.println(message);
                            }
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            while (true) {
                System.out.println("Digite o comando (1: Global, 2: Privado, 3: Listar Usuários, 4: Sair): ");
                String command = scanner.nextLine();
                out.println("/" + command);

                if (command.equals("4")) {
                    break;
                } else if (command.equals("2")) {
                    System.out.println("Digite o destinatário e a mensagem (ex: usuario mensagem): ");
                    String privateMessage = scanner.nextLine();
                    out.println(privateMessage);
                } else if (command.equals("3")) {

                    System.out.println("Solicitando lista de usuários online...");
                } else {

                    System.out.println("Digite a mensagem para o chat global: ");
                    String message = scanner.nextLine();
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}