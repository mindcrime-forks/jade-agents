/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/**
 * LEAP license header to be added
 * SUN license header to be added (?)
 */
package jade.core;

import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;

/**
 * This class allows retrieving configuration-dependent classes.
 * 
 * @author  Federico Bergenti
 * @author  Giovanni Caire - TILAB
 * @version 1.0, 22/11/00
 */
public abstract class Profile {

  /**
     This constant is the name of the property whose value contains a
     boolean indicating if this is the Main Container or a peripheral
     container.
   */
  public static final String MAIN = "main";
  
  /**
     This constant is the name of the property whose value is a String
     indicating the protocol to use to connect to the Main Container.
   */
  public static final String MAIN_PROTO = "proto";

  /**
     This constant is the name of the property whose value is the name
     (or the IP address) of the network host where the JADE Main
     Container is running.
   */
  public static final String MAIN_HOST = "host";

  /**
     This constant is the name of the property whose value contains an
     integer representing the port number where the Main Container is
     listening for container registrations.
   */
  public static final String MAIN_PORT = "port";

  /**
     This constant is the name of the property whose value contains
     the unique platform ID of a JADE platform. Agent GUIDs in JADE
     are made by a platform-unique nickname, the '@' character and the
     platform ID.
   */
  public static final String PLATFORM_ID = "name";
  
    /**
     */
    protected abstract acc getAcc() throws ProfileException;

    /**
     */
    //protected abstract AgentCache getAgentCache() throws ProfileException;

    /**
     */
    //protected abstract MobilityHandler getMobilityHandler() throws ProfileException;

    /**
     */
    //protected abstract IMTPManager getIMTPManager() throws ProfileException;

    /**
     * Retrieve a String value from the configuration properties.
     * If no parameter corresponding to the specified key is found,
     * null is returned.
     * @param key The key identifying the parameter to be retrieved
     * among the configuration properties.
     */
    public abstract String getParameter(String key) throws ProfileException;

    /**
     * Retrieve a list of Specifiers from the configuration properties.
     * Agents, MTPs and other items are specified among the configuration
     * properties in this way.
     * If no list of Specifiers corresponding to the specified key is found,
     * an empty list is returned.
     * @param key The key identifying the list of Specifires to be retrieved
     * among the configuration properties.
     */
    public abstract List getSpecifiers(String key) throws ProfileException;

    /**
     * Utility method that parses a stringified object specifier in the form
     * name:class(arg1, arg2...) and returns
     * a Specifier object.
     * Both the name and the list of arguments are optional.
     * Concrete implementations can take advantage from this method to
     * implement the getSpecifiers() method.
     */
    protected static Specifier parseSpecifier(String specString) 
            throws ProfileException {
        Specifier s = new Specifier();

        // NAME
        int       index1 = specString.indexOf(':');
        int       index2 = specString.indexOf('(');

        if (index2 < 0) {
            index2 = 99999;
        } 

        if (index1 > 0 && index1 < index2) {

            // The name exists, colon exists, and is followed by the class name
            s.setName(specString.substring(0, index1));

            // Skip colon
            index1++;
        } 
        else {

            // No name specified
            index1 = 0;
        } 

        // CLASS
        index2 = specString.indexOf('(', index1);

        if (index2 < 0) {

            // No arguments --> just add the class name
            s.setClassName(specString.substring(index1, specString.length()));
        } 
        else {

            // There are arguments --> add the class name and then parse the args
            s.setClassName(specString.substring(index1, index2));

            // ARGUMENTS
            if (!specString.endsWith(")")) {
                throw new ProfileException("Incorrect specifier \"" 
                                           + specString 
                                           + "\". Missing final parenthesis");
            } 

            // Get everything is in between '(' and ')'
            String args = specString.substring(index2 + 1, 
                                               specString.length() - 1);

            s.setArgs(parseArguments(args));
        } 

        return s;
    } 

    /**
     */
    private static String[] parseArguments(String args) {
        List argList = new ArrayList();
        int  argStart = 0;
        int  argEnd = args.indexOf(',');

        while (argEnd > 0) {
            String arg = args.substring(argStart, argEnd);

            argList.add(arg.trim());

            argStart = argEnd + 1;
            argEnd = args.indexOf(',', argStart);
        } 

        // Last argument
        String arg = args.substring(argStart, args.length());

        argList.add(arg.trim());

        // Convert the List into an Array
        String   arguments[] = new String[argList.size()];
        Iterator it = argList.iterator();
        int      i = 0;

        while (it.hasNext()) {
            arguments[i] = (String) it.next();
            ++i;
        } 

        return arguments;
    } 

}

