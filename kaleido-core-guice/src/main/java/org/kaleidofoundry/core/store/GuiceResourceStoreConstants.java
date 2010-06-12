package org.kaleidofoundry.core.store;

/**
 * @author Jerome RADUGET
 */
public interface GuiceResourceStoreConstants {

   /** interface store declare plugin name */
   public static final String ResourceStorePluginName = "guice-resourceStore";

   /** classpath implementation store plugin name */
   public static final String ClasspathStorePluginName = "guice-classpathStore";
   /** file system implementation store plugin name */
   public static final String FileSystemStorePluginName = "guice-fileStore";
   /** http implementation store plugin name */
   public static final String HttpStorePluginName = "guice-httpStore";
   /** ftp implementation store plugin name */
   public static final String FtpStorePluginName = "guice-ftpStore";
   /** webapp implementation store plugin name */
   public static final String WebappStorePluginName = "guice-webappStore";
   /** jdbc implementation clob store plugin name */
   public static final String ClobJdbcStorePluginName = "guice-jdbcStore";
   /** jpa implementation clob store plugin name */
   public static final String ClobJpaStorePluginName = "guice-jpaStore";
   /** mocked in memory implementation plugin name */
   public static final String MemoryStorePluginName = "guice-memoryStore";

}
