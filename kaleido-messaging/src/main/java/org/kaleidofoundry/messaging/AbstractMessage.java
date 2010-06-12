package org.kaleidofoundry.messaging;

import java.util.List;
import java.util.Map;

/**
 * Abstract Message
 * 
 * @author Jerome RADUGET
 */
public abstract class AbstractMessage implements Message {

   private Map<String, Object> parameters;

   public AbstractMessage() {
   }

   public AbstractMessage(final Map<String, Object> parameters) {
	this.parameters = parameters;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Message#getParameters()
    */
   public Map<String, Object> getParameters() {
	return parameters;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Message#setParameters(java.util.Map)
    */
   public void setParameters(final Map<String, Object> parameters) {
	this.parameters = parameters;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
	return toString(null);
   }

   /**
    * @param exeptions parametres � exclure dans la sortie
    * @return
    */
   public String toString(final List<String> exeptions) {

	final StringBuffer str = new StringBuffer();

	if (getParameters() != null) {
	   str.append("{");
	   for (final String param : getParameters().keySet()) {
		if (exeptions == null || !exeptions.contains(param)) {
		   str.append(param).append("=").append(getParameters().get(param)).append(" ; ");
		}
	   }
	   str.append("}");
	}

	return str.toString();
   }

   @Override
   public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
	return result;
   }

   @Override
   public boolean equals(final Object obj) {
	if (this == obj) return true;
	if (obj == null) return false;
	if (getClass() != obj.getClass()) return false;
	final AbstractMessage other = (AbstractMessage) obj;
	if (parameters == null) {
	   if (other.parameters != null) return false;
	} else if (!parameters.equals(other.parameters)) return false;
	return true;
   }

}
