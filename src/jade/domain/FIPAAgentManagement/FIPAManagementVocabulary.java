package jade.domain.FIPAAgentManagement;

public interface FIPAManagementVocabulary extends ExceptionVocabulary {
	
  /**
    A symbolic constant, containing the name of this ontology.
  */
  public static final String NAME = "FIPA-Agent-Management";

  // Concepts
  public static final String DFAGENTDESCRIPTION = "df-agent-description";
  public static final String DFAGENTDESCRIPTION_NAME	= "name";
  public static final String DFAGENTDESCRIPTION_SERVICES = "services";
  public static final String DFAGENTDESCRIPTION_PROTOCOLS = "protocols";
  public static final String DFAGENTDESCRIPTION_ONTOLOGIES = "ontologies";
  public static final String DFAGENTDESCRIPTION_LANGUAGES = "languages";
  public static final String DFAGENTDESCRIPTION_LEASE_TIME = "lease-time";

  public static final String SERVICEDESCRIPTION	= "service-description";
  public static final String SERVICEDESCRIPTION_NAME = "name";
  public static final String SERVICEDESCRIPTION_TYPE = "type";
  public static final String SERVICEDESCRIPTION_OWNERSHIP = "ownership";
  public static final String SERVICEDESCRIPTION_PROTOCOLS = "protocols";
  public static final String SERVICEDESCRIPTION_ONTOLOGIES = "ontologies";
  public static final String SERVICEDESCRIPTION_LANGUAGES = "languages";
  public static final String SERVICEDESCRIPTION_PROPERTIES = "properties";
  
  public static final String SEARCHCONSTRAINTS = "search-constraints";
  public static final String SEARCHCONSTRAINTS_MAX_DEPTH = "max-depth";
  public static final String SEARCHCONSTRAINTS_MAX_RESULTS = "max-results";
  public static final String SEARCHCONSTRAINTS_SEARCH_ID = "search-id";
  
  public static final String AMSAGENTDESCRIPTION = "ams-agent-description";
  public static final String AMSAGENTDESCRIPTION_NAME = "name";
  public static final String AMSAGENTDESCRIPTION_OWNERSHIP = "ownership";
  public static final String AMSAGENTDESCRIPTION_STATE = "state";	

  public static final String PROPERTY = "property";
  public static final String PROPERTY_NAME = "name";
  public static final String PROPERTY_VALUE = "value";

  public static final String ENVELOPE 					= "envelope";
  public static final String ENVELOPE_TO 				= "to";
  public static final String ENVELOPE_FROM 				= "from";
  public static final String ENVELOPE_COMMENTS			= "comments";
  public static final String ENVELOPE_ACLREPRESENTATION = "acl-representation";
  public static final String ENVELOPE_PAYLOADLENGTH		= "payload-length";
  public static final String ENVELOPE_PAYLOADENCODING	= "payload-encoding";
  public static final String ENVELOPE_DATE				= "date";
  public static final String ENVELOPE_ENCRYPTED			= "encrypted";
  public static final String ENVELOPE_INTENDEDRECEIVER	= "intended-receiver";
  public static final String ENVELOPE_TRANSPORTBEHAVIOUR= "transport-behaviour";
  public static final String ENVELOPE_STAMPS			= "stamps";
 
  public static final String RECEIVEDOBJECT				= "received-object";
  public static final String RECEIVEDOBJECT_BY			= "by";
  public static final String RECEIVEDOBJECT_FROM		= "from";
  public static final String RECEIVEDOBJECT_DATE		= "date";
  public static final String RECEIVEDOBJECT_ID		 	= "id";
  public static final String RECEIVEDOBJECT_VIA	 		= "via";

  public static final String APDESCRIPTION					= "ap-description";
  public static final String APDESCRIPTION_NAME				= "name";
  public static final String APDESCRIPTION_DYNAMIC			= "dynamic";
  public static final String APDESCRIPTION_MOBILITY			= "mobility";
  public static final String APDESCRIPTION_TRANSPORTPROFILE = "transport-profile";

  public static final String APTRANSPORTDESCRIPTION 			= "ap-transport-description";
  public static final String APTRANSPORTDESCRIPTION_AVAILABLEMTPS	= "available-mtps";

  public static final String MTPDESCRIPTION					= "mtp-description";
  public static final String MTPDESCRIPTION_PROFILE			= "profile";
  public static final String MTPDESCRIPTION_NAME			= "mtp-name";
  public static final String MTPDESCRIPTION_ADDRESSES	    = "addresses"; 
 
  // Actions
  public static final String REGISTER = "register";
  public static final String REGISTER_DESCRIPTION = "description";
  
  public static final String DEREGISTER	= "deregister";
  public static final String DEREGISTER_DESCRIPTION = "description";
  
  public static final String MODIFY	= "modify";
  public static final String MODIFY_DESCRIPTION = "description";
  
  public static final String SEARCH	= "search";
  public static final String SEARCH_DESCRIPTION = "description";
  public static final String SEARCH_CONSTRAINTS = "constraints";
  
  public static final String GETDESCRIPTION = "get-description";
  
}
