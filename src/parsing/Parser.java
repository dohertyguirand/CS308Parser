package parsing;

import execution.Command;
import execution.CommandFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

//import execution.MultipleTurtlesCommand;
//import execution.MultipleTurtlesCommandFactory;
import model.*;

public class Parser {

    private static final String RESOURCES = "resources/languages";
    private static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
    private static final String ERROR_STRING = "ErrorInput";
    private static final String MAKE_VAR = "MakeVariable";
    private List<Map.Entry<String, Pattern>> mySymbols;
    private CommandFactory factory = new CommandFactory();
//    private MultipleTurtlesCommandFactory  mtFactory = new MultipleTurtlesCommandFactory();
    private ResourceBundle resourceBundle;

    private String myLanguage;
    //private ConsoleModel myConsoleModel;
    private Double lastReturnValue;

    private int currentIndex;
    private String poppedCommand;

    //private TurtleModelContainer turtleModelContainer;

    private ModelContainer allModels;
    private VariableModel myVariableModel;
    private Map<String, MethodModel> myMethodModels;

    private Stack<String> groupCmds = new Stack<>();
    private int reservedIxs = 0;

    // Loop mappings specifies how many sets of brackets each command type has
    // e.g., repeat 3 [ cmds ] has 1 set of brackets while dotimes [3] [ cmds ] has 2 sets of brackets
    private static final List<String> MTCOMMANDS = new ArrayList<>(){{
        add("Repeat");
        add("DoTimes");
        add("If");
        add("IfElse");
        add("MakeUserInstruction");
        add("Tell");
        add("Turtles");
        add("Ask");
    }};


    private static final Map<String, Integer> LOOP_MAPPINGS = new HashMap<String, Integer>() {{
        put("Repeat", 1);
        put("DoTimes", 2);
        put("For", 2);
        put("If", 1);
        put("IfElse", 2);
        put("MakeUserInstruction", 2);
        put("Tell", 1);
        put("Ask", 2);
    }};


    public Parser(String commands, String language, ModelContainer allModels) {

        this.allModels = allModels;
        this.myVariableModel = allModels.getVariableModel();
        this.myMethodModels = allModels.getMethodModels();
        this.myLanguage = language;
        resourceBundle = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
        addPatterns(language);
        this.currentIndex = 0;
        this.poppedCommand = "";
        splitAndParseText(commands,allModels.getTurtleModelContainer());
    }

    private boolean validateMessage() {
        return false;
    }

    /**
     * splits the commands by the tell command  so that the parser runs on the appropiate turtlemodels
     * @param commands string of user given commands
     * @param turtlemodelcontainer the TurtleModelContainer that stores all of the turtlemodels
     */
    private void splitAndParseText(String commands, TurtleModelContainer turtlemodelcontainer) {
        boolean firstOne = true;
        List<String> splitByTell = new ArrayList<>(Arrays.asList(commands.split("tell")));
        for(String commandSeq : splitByTell){
            commandSeq = removeComments(commandSeq);
            if(commands.contains("tell")){
                if(!firstOne){
                    commandSeq = "tell " + commandSeq;
                }
            }
            firstOne = false;
            if(commandSeq.equals("")) continue;
            for(currentIndex = 0; currentIndex < turtlemodelcontainer.getActiveTurtles().size(); currentIndex ++){
                try {
                    parseText(commandSeq,turtlemodelcontainer.getActiveTurtles().get(currentIndex));
                } catch( Exception e){
//            System.out.println("error");
                    String message = resourceBundle.getString(ERROR_STRING);
                    allModels.setConsoleErrorMessage(message);
                    e.printStackTrace();
                }


            }}}



    private void parseText(String commands, TurtleModel currentTurtle)
        throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {


        List<String> symbolList = Arrays
            .asList(String.join(" ", commands.toLowerCase().split("\n")).split("[ ]+"));
        // separate the user input string into list where each element is either a command or number

        Stack<String> cmdStack = new Stack<>();
        Stack<String> argStack = new Stack<>();

        // important variable which specifies the end index of the last closing bracket of a command; the parser will skip any cursor which is less than this loopEndIndex
        int loopEndIndex = -1;

        // handles the case where parser was called on the parameters of a loop cmd (e.g. dotimes [ :x 3 ] ) and store a return value as the last parameter
        setLoopReturnValue(symbolList);

        for (int cursor = 0; cursor < symbolList.size(); cursor++) {

            if (cursor > loopEndIndex) { // make sure that we DO NOT parse any symbols in between the last stored loopEndIndex ( we will send the loop body to another parser via a cmd); only parse if our current cursor is after the latest loopEndIndex
                // symbol represents the space delimited command or parameter (e.g. fd or 30 or :variable)
                String symbol = symbolList.get(cursor).strip();

                System.out.println("symbol: "+symbol);
                // push the symbol to the correct stack (cmdStack or argStack based on its regular expression match).
                // if the command is a loop command, find the index of the closing loop bracket and store it in loopEndIndex so that the program can skip all symbols until we reach this index
                loopEndIndex = pushToStack(symbolList, cmdStack, argStack, loopEndIndex, cursor, symbol, currentTurtle);

                printDebugging(cmdStack,argStack,cursor);


                // pop commands and args off the stack and handle them
                handleStacks(currentTurtle, cmdStack, argStack);

            }

        }

    }

    private void handleStacks(TurtleModel currentTurtle, Stack<String> cmdStack,
        Stack<String> argStack)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        while (groupCmds.isEmpty() && cmdAndArgsExist(cmdStack, argStack)) {
            if(myMethodModels.containsKey(cmdStack.peek())){ // if the popped command is a user generated method
                parseUserMethod(currentTurtle, cmdStack, argStack);
            } else if (checkInsufficientParameters(cmdStack, argStack)) {
                break;
            } else { // if the popped command is a default method
                parseDefaultMethod(currentTurtle, cmdStack, argStack, cmdStack.pop());
            }
        }
    }

    private void parseDefaultMethod(TurtleModel currentTurtle, Stack<String> cmdStack,
        Stack<String> argStack, String poppedCommand)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<String> params = new ArrayList<>();
        Double returnValue = 0.0;

        Command cmdToExecute = factory.getCommand(getSymbol(poppedCommand));

        System.out.println(argStack);
        while (cmdToExecute.getNumParams() > params.size()) {
            String popped = argStack.pop();

            // check if the argument is a variable, and convert it to double if the command is not "MAKE"
            if (!cmdToExecute.getClass().getSimpleName().equals(MAKE_VAR)
                && popped.matches(":[a-zA-Z_]+")) {
                popped = Double.toString(myVariableModel.getValue(popped));
            }

            params.add(popped);
        }

        Collections.reverse(params);

        // EXECUTE THE COMMAND AND STORE THE RETURN VALUE IN INSTANCE VARIABLE AND ON ARGUMENT STACK
        returnValue = cmdToExecute
            .execute(params, currentTurtle, allModels);

        this.lastReturnValue = returnValue;
        if (!cmdStack.isEmpty() || (!this.groupCmds.isEmpty() && cmdToExecute.getNumParams()>1) && !cmdToExecute.getClass().getSimpleName().equals(MAKE_VAR)) {
            argStack.push(returnValue.toString());
            if (groupCmds.size()==1) reservedIxs+=1;
        }
    }

    private boolean checkInsufficientParameters(Stack<String> cmdStack, Stack<String> argStack)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return cmdStack.size() >= 2 &&
            (
                getNumParams(cmdStack.size() - 2, cmdStack) == 1 && getNumParams(cmdStack.size() - 2, cmdStack) > argStack.size()
                    // and fd 30 bk 20
                    ||
                    getNumParams(cmdStack.size() - 2, cmdStack) == 2 && getNumParams(cmdStack.size() - 2, cmdStack) >= argStack.size() // make :var sum 20 30
            );
    }

    private void parseUserMethod(TurtleModel currentTurtle, Stack<String> cmdStack,
        Stack<String> argStack)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String methodName = cmdStack.pop();
        System.out.println("running method: "+methodName);

        MethodModel myMethodModel = myMethodModels.get(methodName);

        List<String> params = new ArrayList<>();
        while (myMethodModel.getNumVariables() > params.size()) {
            String popped = argStack.pop();
            params.add(popped);
        }
        Collections.reverse(params);

        for(int i = 0; i < params.size(); i++) {
            List <String> varNameAndValue = new ArrayList<>();
            varNameAndValue.add(myMethodModel.getVariableName(i));

            String value = params.get(i);
            if(value.matches(":[a-zA-Z_]+")) {
                value = Double.toString(myVariableModel.getValue(value));
            }
            varNameAndValue.add(value);
            factory.getCommand(getSymbol("make")).execute(varNameAndValue, currentTurtle, allModels);
        }

        Parser parser = new Parser(myMethodModel.getMethodBody(), myLanguage, allModels );
        lastReturnValue = parser.getLastReturnValue();
    }

    private boolean cmdAndArgsExist(Stack<String> cmdStack, Stack<String> argStack)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return !cmdStack.isEmpty() &&
            (
                (myMethodModels.containsKey(cmdStack.peek()) && argStack.size() >= myMethodModels.get(cmdStack.peek()).getNumVariables())
                    ||
                    (!myMethodModels.containsKey(cmdStack.peek()) && argStack.size() >= getNumParams(cmdStack.peek())) // for standard command
            );
    }

    private int pushToStack(List<String> symbolList, Stack<String> cmdStack, Stack<String> argStack,
        int loopEndIndex, int cursor, String symbol, TurtleModel currentTurtle)
        throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (symbol.equals("(")){
//            loopEndIndex = new GroupParser(myLanguage).handleGroup(symbolList, cursor, symbol);
            loopEndIndex = cursor + 1;
            this.groupCmds.push(symbolList.get(cursor+1));
        } else if (symbol.equals(")")) {
            System.out.print("Group Comamnds");
            System.out.println(groupCmds);
            String groupCmd = groupCmds.pop();
            while (argStack.size()>=1 && (groupCmds.isEmpty() || argStack.size()>reservedIxs) ) {
                parseDefaultMethod(currentTurtle, cmdStack, argStack, groupCmd);
            }
        }
        else if (LOOP_MAPPINGS.containsKey(getSymbol(symbol))) {
            loopEndIndex = handleLoop(symbolList, cmdStack, argStack, cursor, symbol);
        }
        else if (symbol.matches("^[-+*\\/a-zA-Z_]+(\\?)?$")) {
            cmdStack.push(symbol); //factory.getCommand(getSymbol(symbol))
        } else { // if symbol is a number
            argStack.push(symbol);
        }
        return loopEndIndex;
    }



    private void setLoopReturnValue(List<String> symbolList) {
        if (symbolList.size() == 1 && !symbolList.get(0)
            .matches("[-+*\\/]?[a-zA-Z_]+(\\?)?")) { // for parsing within loop guard
            if (symbolList.get(0).matches(":[a-zA-Z_]+")) {
                lastReturnValue = myVariableModel.getValue(symbolList.get(0));
            } else {
                lastReturnValue = Double.parseDouble(symbolList.get(0));
            }
        }
    }

    private String removeComments(String commands) {
        List<String> fullList = Arrays.asList(commands.split("\n"));
        List<String> newList = new ArrayList<String>(fullList);
        for(String line: fullList){
            line = line.strip();
            if(line.startsWith("#")){
                newList.remove(line);
            }
        }
        commands = String.join(" ", newList);
        return commands;
    }


    private int getNumParams(int index, Stack<String> commandStack)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String command = commandStack.get(index);

//        if(MTCOMMANDS.contains(command)){
//            return mtFactory.getCommand(getSymbol(command)).getNumParams();
//        }

        return factory.getCommand(getSymbol(command)).getNumParams();
    }

    private int getNumParams(String symbol)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
//        System.out.println("Getting factory for symbol: "+ symbol);
//        if(MTCOMMANDS.contains(symbol)) return mtFactory.getCommand(getSymbol(symbol)).getNumParams();

        return factory.getCommand(getSymbol(symbol)).getNumParams();
    }

    public Double getLastReturnValue () {
        return lastReturnValue;
    }


    private int handleLoop
        (List < String > symbolList, Stack < String > cmdStack, Stack < String > argStack,
            int cursor, String symbol)
        throws
        ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        cmdStack.push(symbol);
        int loopEndIndex = getLoopEndIndex(symbolList, cursor, symbol);

        List<String> argsWithLanguage = new ArrayList<>(
            symbolList.subList(cursor + 1, loopEndIndex));
        System.out.println("ldaskjfasiodfjasiodjf");
        System.out.println(cursor);
        System.out.println(loopEndIndex);
        argsWithLanguage.add(0, myLanguage);
        argStack.push(String.join(" ", argsWithLanguage));
        return loopEndIndex;
    }

    private int getLoopEndIndex (List < String > symbolList,int loopStartIndex, String cmdString)
    {
        int openBracketCount = 0;
        int closeBracketCount = 0;

        int cursor = loopStartIndex; //the index of the command symbol
        while (openBracketCount > closeBracketCount || closeBracketCount < LOOP_MAPPINGS
            .get(getSymbol(cmdString))) {
            cursor += 1;
            String symbol = symbolList.get(cursor).strip();
            if (symbol.equals("]")) {
                closeBracketCount += 1;
            } else if (symbol.equals("[")) {
                openBracketCount += 1;
            }
        }
        return cursor;
    }

    private List<String> getLoopBody (List < String > symbolList,int loopStartIndex){
        int loopEndIndex = -1;
        for (int i = 0; i < symbolList.size(); i++) {
            if (symbolList.get(i).equals("]")) {
                loopEndIndex = i;
            }
        }
        return symbolList.subList(loopStartIndex, loopEndIndex - 1);
    }

    /**
     * Adds the given resource file to this language's recognized types
     */
    public void addPatterns (String syntax){
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
    private String getSymbol (String text){
        final String ERROR = "NoMatch";
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
    private boolean match (String text, Pattern regex){
        // THIS IS THE IMPORTANT LINE
        return regex.matcher(text).matches();
    }

    private void printDebugging(Stack<String> cmdStack, Stack<String> argStack, int cursor)
        throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //                printDebugging(cmdStack, argStack, cursor);

        System.out.println("Cursor: "+cursor);

        System.out.print("Cmd: ");
        System.out.println(cmdStack);
        System.out.print("Arg: ");
        System.out.println(argStack);
        System.out.println();
    }
}
