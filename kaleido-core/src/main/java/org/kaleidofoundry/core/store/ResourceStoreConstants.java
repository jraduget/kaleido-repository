package org.kaleidofoundry.core.store;

/**
 * @author Jerome RADUGET
 */
public interface ResourceStoreConstants {

   /** interface store declare plugin name */
   public static final String ResourceStorePluginName = "resourceStore";

   /** classpath implementation store plugin name */
   public static final String ClasspathStorePluginName = "classpathStore";
   /** file system implementation store plugin name */
   public static final String FileSystemStorePluginName = "fileStore";
   /** http implementation store plugin name */
   public static final String HttpStorePluginName = "httpStore";
   /** ftp implementation store plugin name */
   public static final String FtpStorePluginName = "ftpStore";
   /** webapp implementation store plugin name */
   public static final String WebappStorePluginName = "webappStore";
   /** jdbc implementation clob store plugin name */
   public static final String ClobJdbcStorePluginName = "jdbcStore";
   /** jpa implementation clob store plugin name */
   public static final String ClobJpaStorePluginName = "jpaStore";
   /** mocked in memory implementation plugin name */
   public static final String MemoryStorePluginName = "memoryStore";

}
