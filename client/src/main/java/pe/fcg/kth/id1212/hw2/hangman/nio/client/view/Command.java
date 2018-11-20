package pe.fcg.kth.id1212.hw2.hangman.nio.client.view;

class Command {
    public enum Type {
        QUIT, CONNECT, GUESS, START, UNKNOWN
    }
    static final int HOST_POS = 0;
    static final int PORT_POS = 1;
    static final int GUESS_POS = 0;
    private Type type;
    private String[] args;

    Command(String userInput) {
        parse(userInput);
    }

    private void parse(String userInput) {
        String[] parts = userInput.split("\\s+");
        try {
            type = Type.valueOf(parts[0].toUpperCase());
        } catch(Exception e) {
            type = Type.UNKNOWN;
        }
        args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);
    }

    Type getType() {
        return type;
    }

    String[] getArgs() {
        return args;
    }
}
