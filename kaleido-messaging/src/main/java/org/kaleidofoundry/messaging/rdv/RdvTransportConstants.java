package org.kaleidofoundry.messaging.rdv;

/**
 * Constantes pour Transport et Listener RDV
 * 
 * @author Jerome RADUGET
 */
public interface RdvTransportConstants {

   /** Separateur entre les sujets écoutés */
   public static final String Subjects_Separator = ";";

   /** Chemin propriété pour avoir le service RDV */
   public static final String PATH_KEY_Service = "tibco.rdv.service";
   /** Chemin propriété pour avoir le network RDV */
   public static final String PATH_KEY_Network = "tibco.rdv.network";
   /** Chemin propriété pour avoir le daemon RDV */
   public static final String PATH_KEY_Daemon = "tibco.rdv.daemon";
   /** Chemin propriété pour avoir le type de transport RDV @see {@link RdvTransportTypeEnum} */
   public static final String PATH_KEY_Type = "tibco.rdv.type";
   /** Chemin propriété pour avoir le cmname du transport RDV certified */
   public static final String PATH_KEY_CMname = "tibco.rdv.cmname";
   /** Chemin propriété pour avoir le timeout du transport RDV certified */
   public static final String PATH_KEY_Timeout = "tibco.rdv.timeout";

   /** Chemin propriété pour avoir le sujet du listener RDV */
   public static final String PATH_KEY_Subject = "tibco.rdv.sujects";

}
