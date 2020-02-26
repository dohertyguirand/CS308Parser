package parsing;

import execution.Command;
import execution.CommandFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import model.TurtleModel;

public class Parser {
    private static final String RESOURCES = "resources/languages";
    private static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
    private List<Map.Entry<String, Pattern>> mySymbols;
    private CommandFactory factory = new CommandFactory();

    private TurtleModel myTurtleModel;
    public Parser (String commands ,String language, TurtleModel myTurtleModel) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException {
        this.myTurtleModel = myTurtleModel;
        addPatterns(language);
        parseText(commands);


    }

    private boolean validateMessage(){
        return false;
    }

    private void parseText ( String commands) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
        List<String>inputCommands = Arrays.asList(String.join(" ",commands.toLowerCase().split("\n")).split("[ ]+"));

//        for (String line : lines) {
//            List<String> commandAndParams = Arrays.asList(line.strip().split("[ ]+"));
//
//            //extract the command name (e.g. FD)
//            String command = commandAndParams.get(0);
//            if(!command.matches("^[a-zA-Z]+$")) {
//                System.out.println("Invalid command.");
//                return;
//            }
//
//            // populate a list of the parameters
//            List<String> params = new ArrayList<>();
//            // check if there are parameters included with the command symbol
//            if(commandAndParams.size()>1) {
//                params = commandAndParams.subList(1, commandAndParams.size());
//            }
//            // error check the parameters for invalid nonnumeric charactrs
//            for(String param: params){
//                // make sure string is a decimal
//                System.out.println("Parameter: " + param);
//                if (!param.matches("^\\-?[0-9]*\\.?[0-9]*$")) {
//                    System.out.println("Invalid Parameter.");
//                    return;
//                }
//            }
//
//            // Execute the command
//            String symbol = getSymbol(command);
//            Command factoryCommand = factory.getCommand(getSymbol(symbol));
//            if(factoryCommand.getNumParams()==params.size()){
//                factoryCommand.execute(params, myTurtleModel);
//            } else {
//                System.out.println("Invalid number of parameters.");
//            }
//
//        }
        //FIXME: Only works for commands with only one parameter of type int
        Stack <List<String>> stack = new Stack();

        Map <String, String> variables = new HashMap<>();

        stack.push(inputCommands);

        while(!stack.isEmpty()){
            List <String> symbolList = stack.pop();

            Stack <Command> cmdStack = new Stack<>();
            Stack <Double> argStack = new Stack<>();

            // iterate thru commands in popped element;
            int cursor = 0;

            String symbol = symbolList.get(cursor).strip();
            Command factoryCommand = factory.getCommand(getSymbol(symbol));
            cmdStack.push(factoryCommand);

            while (!cmdStack.isEmpty()) {
                if( argStack.size() >= cmdStack.peek().getNumParams()){
                    Command cmdToExecute = cmdStack.pop();
                    List<Double> params = new ArrayList<>();
                    while (cmdToExecute.getNumParams() > params.size()){
                        Double popped = argStack.pop();
                        params.add(popped);

                    }
                    argStack.push(cmdToExecute.execute(params, myTurtleModel));
                } else if (cursor<symbolList.size()-1){
                    cursor++;
                    symbol = symbolList.get(cursor).strip();
                    //if the symbol is a command
                    if (symbol.matches("^[a-zA-Z]+$")) {
                        cmdStack.push(factory.getCommand(getSymbol(symbol)));
                    } else { // if symbol is a number
                        argStack.push(Double.parseDouble(symbol));
                    }
                }
            }

//                for(int i = 0; i < symbolList.size(); i++){
//                    String symbol = symbolList.get(i).strip();
//
//                    switch(symbol){
//                        case "REPEAT":
//                            int numLoops = Integer.parseInt(symbolList.get(i+1));
//                            for(int j = 0; j < numLoops; j++){
//                                stack.push(getLoopBody(symbolList, i+1));
//                            }
//                            break;
//                        case "DOTIMES":
//                            break;
//                        case "FOR":
//                            break;
//                    }
//                }


        }
    }



    private List<String> getLoopBody(List<String> symbolList, int loopStartIndex){
        int loopEndIndex = -1;
        for (int i = 0; i < symbolList.size(); i++){
            if(symbolList.get(i).equals("]")){
                loopEndIndex = i;
            }
        }
        return symbolList.subList(loopStartIndex, loopEndIndex-1);
    }

        /**
         * Adds the given resource file to this language's recognized types
         */
        public void addPatterns(String syntax) {
            mySymbols = new ArrayList<>();
            ResourceBundle resources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + syntax);
            for (String key : Collections.list(resources.getKeys())) {
                String regex = resources.getString(key);
                mySymbols.add(new AbstractMap.SimpleEntry<>(key,
                        // THIS IS THE IMPORTANT LINE
                        Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));
            }
        }

        /**
         * Returns language's type associated with the given text if one exists
         */
        private String getSymbol (String text) {
            final String ERROR = "NO MATCH";
            for (Map.Entry<String, Pattern> e : mySymbols) {
                if (match(text, e.getValue())) {
//                    System.out.println(e.getKey());
                    return e.getKey();
                }
            }
            // FIXME: perhaps throw an exception instead
            return ERROR;
        }

        // Returns true if the given text matches the given regular expression pattern
        private boolean match (String text, Pattern regex) {
            // THIS IS THE IMPORTANT LINE
            return regex.matcher(text).matches();
        }
    }



