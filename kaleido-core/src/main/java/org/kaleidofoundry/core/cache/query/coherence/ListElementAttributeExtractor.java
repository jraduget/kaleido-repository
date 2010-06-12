/*
 * $License$
 */
package org.kaleidofoundry.core.cache.query.coherence;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.tangosol.io.ExternalizableLite;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.ValueExtractor;

/**
 * 
 * @author Jerome RADUGET
 * 
 * http://forums.oracle.com/forums/thread.jspa?messageID=3899321&#3899321
 */
public class ListElementAttributeExtractor implements ExternalizableLite, ValueExtractor{

   private static final long serialVersionUID = 4366987570866754567L;

   private ValueExtractor iterableExtractor;   
   private ValueExtractor attributeExtractor;
   
   /**
    * 
    * @param iterableExtractor
    * @param attributeExtractor
    */
   public ListElementAttributeExtractor(ValueExtractor iterableExtractor, ValueExtractor attributeExtractor)
   {
       super();
       this.iterableExtractor = iterableExtractor;
       this.attributeExtractor = attributeExtractor;
   }

   /*
    * (non-Javadoc)
    * @see com.tangosol.io.ExternalizableLite#readExternal(java.io.DataInput)
    */
   @Override
   public void readExternal(DataInput input) throws IOException
   {
       iterableExtractor = (ValueExtractor)ExternalizableHelper.readObject(input);
       attributeExtractor = (ValueExtractor)ExternalizableHelper.readObject(input);
   }

   /*
    * (non-Javadoc)
    * @see com.tangosol.io.ExternalizableLite#writeExternal(java.io.DataOutput)
    */
   @Override
   public void writeExternal(DataOutput output) throws IOException
   {
       ExternalizableHelper.writeObject(output, iterableExtractor);
       ExternalizableHelper.writeObject(output, attributeExtractor);
   }

   /*
    * (non-Javadoc)
    * @see com.tangosol.util.ValueExtractor#extract(java.lang.Object)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Object extract(Object object)
   {
       ValueExtractor ext = attributeExtractor;

       java.util.Collection<Object> result = null;
       
       Object iterable = iterableExtractor.extract(object);
       
       if (iterable instanceof Object[]) {
           
           Object[] arr = (Object[])iterable; 
           result = new ArrayList(arr.length);
           for (Object element : arr) {
              result.add(ext.extract(element)); 
           }
           
       } else if (iterable instanceof Collection) {
           
           Collection collection = (Collection)iterable; 
           result = new ArrayList(collection.size());
           for (Object element : collection) {
              result.add(ext.extract(element)); 
           }
       }
       
       return result;
   }
   
   /*
    * (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
       if (obj != null && obj.getClass().equals(getClass())) {
           ListElementAttributeExtractor other = (ListElementAttributeExtractor)obj;
           return attributeExtractor.equals(other.attributeExtractor) && iterableExtractor.equals(other.iterableExtractor);
       }
       return false;
   }
   
   /*
    * (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
       return attributeExtractor.hashCode() ^ iterableExtractor.hashCode();
   }
}
