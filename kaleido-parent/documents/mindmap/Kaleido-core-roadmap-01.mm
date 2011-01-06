<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1262523247000" ID="Freemind_Link_38240127" MODIFIED="1279799867260" TEXT="KaleidoFoundry core">
<font BOLD="true" ITALIC="true" NAME="Dialog" SIZE="14"/>
<node COLOR="#000000" CREATED="1263130504000" ID="Freemind_Link_38815464" MODIFIED="1277133868443" POSITION="left" TEXT="file store">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274170058864" ID="ID_1594601089" MODIFIED="1277133566210" STYLE="fork" TEXT="objectives">
<node CREATED="1274170066317" ID="ID_1128149001" MODIFIED="1277065083655" STYLE="bubble" TEXT="to provide an uniform access to a resource file (configuration, template, ...)"/>
<node CREATED="1274170094129" ID="ID_1172682028" MODIFIED="1277065000674" STYLE="bubble" TEXT="to support different back end : file system, ftp, http, clob, ..."/>
</node>
<node CREATED="1265977562627" ID="Freemind_Link_72346299" MODIFIED="1288858031797" TEXT="implementations">
<node COLOR="#338800" CREATED="1274170207846" ID="ID_1485872953" MODIFIED="1274172597550" TEXT="ClasspathFileStore"/>
<node COLOR="#338800" CREATED="1274170224096" ID="ID_1332461641" MODIFIED="1274172597550" TEXT="FileSystemStore"/>
<node COLOR="#338800" CREATED="1274170233705" ID="ID_977496062" MODIFIED="1274172597550" TEXT="FtpStore"/>
<node COLOR="#338800" CREATED="1274170241752" ID="ID_1332115076" MODIFIED="1274172597550" TEXT="HttpFileStore"/>
<node COLOR="#338800" CREATED="1274170252299" ID="ID_1390417665" MODIFIED="1274172597534" TEXT="JpaFileStore"/>
<node COLOR="#338800" CREATED="1274170272220" ID="ID_204522737" MODIFIED="1274172597534" TEXT="MemoryFileStore"/>
<node COLOR="#ff0000" CREATED="1288858033219" ID="ID_188508210" MODIFIED="1288858070000" TEXT="SecureFtpStore"/>
<node CREATED="1277132539498" ID="ID_341829723" MODIFIED="1277132541076" TEXT="..."/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114329000" ID="Freemind_Link_38802849" MODIFIED="1277133888333" POSITION="left" TEXT="cache">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274171932980" ID="ID_1061235509" MODIFIED="1277064651880" TEXT="objectives">
<node CREATED="1274171948324" ID="ID_928887048" MODIFIED="1279693064902" STYLE="bubble" TEXT="to provide a simple and independent interface to access cache datas, local or clustered"/>
<node CREATED="1274171972621" ID="ID_239610198" MODIFIED="1277065574389" STYLE="bubble" TEXT="implementations are a &quot;plug bridge&quot; to a open source or commercial solution"/>
<node CREATED="1274172294257" ID="ID_613471733" MODIFIED="1277066002691" STYLE="bubble" TEXT="to be no more dependent in code and import of an intrusive implementation"/>
<node CREATED="1274172260820" ID="ID_167134996" MODIFIED="1277065986898" STYLE="bubble" TEXT="only update external cache configuration to adapt to your need (cluster, local, ...)"/>
</node>
<node CREATED="1265890816941" ID="Freemind_Link_235149795" MODIFIED="1277065034673" TEXT="implementations">
<node COLOR="#338800" CREATED="1274172539707" ID="ID_466824154" MODIFIED="1274172585457" TEXT="default local"/>
<node COLOR="#338800" CREATED="1274172480723" ID="ID_1768588229" MODIFIED="1274172585472" TEXT="ehcache 1.x to 2.x"/>
<node COLOR="#338800" CREATED="1274172495051" ID="ID_1671494473" MODIFIED="1274172585472" TEXT="jboss cache 3.X"/>
<node COLOR="#338800" CREATED="1274172516754" ID="ID_1142104684" MODIFIED="1274172585472" TEXT="jboss infinispan 4.x"/>
<node COLOR="#338800" CREATED="1274172464146" ID="ID_1719665764" MODIFIED="1274172585472" TEXT="coherence 3.x"/>
<node COLOR="#ff0000" CREATED="1274172562332" ID="ID_1348216642" MODIFIED="1277118062778" TEXT="gigaspace"/>
<node CREATED="1277118828945" ID="ID_965190059" MODIFIED="1277118830086" TEXT="..."/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114081000" ID="Freemind_Link_38802741" MODIFIED="1277128688913" POSITION="left" TEXT="configuration">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1265977922834" ID="Freemind_Link_1917486617" MODIFIED="1274171775201" TEXT="objectives">
<node CREATED="1265977931741" ID="Freemind_Link_1040325902" MODIFIED="1277118317964" STYLE="bubble" TEXT="to provide unified runtime configuration interface for your application">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1265977969866" ID="Freemind_Link_668602541" MODIFIED="1277119344176" STYLE="bubble" TEXT="to expose and manage configuration via jmx, web, web service">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1274171777061" ID="ID_611464567" LINK="#Freemind_Link_38802849" MODIFIED="1279693110231" STYLE="bubble" TEXT="to allow transparent use in a cluster (it uses cache stack, define your own cache policies)">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1265977948585" ID="Freemind_Link_619061248" LINK="#Freemind_Link_38815464" MODIFIED="1277133868443" STYLE="bubble" TEXT="based on file store (file://, http://, ftp://, jpa:/ ...) to access configuration datas (xml, prop,...)">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114307000" ID="Freemind_Link_38802815" MODIFIED="1277065041478" TEXT="implementations">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#338800" CREATED="1263133328000" ID="Freemind_Link_38817175" MODIFIED="1274172615378" TEXT="PropertiesConfiguration (default or xml)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1274169638025" ID="ID_1628091349" MODIFIED="1274172615378" TEXT="XmlConfiguration"/>
<node COLOR="#338800" CREATED="1263114098000" ID="Freemind_Link_38802743" MODIFIED="1274172615378" TEXT="JavaSystemConfiguration">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1263114284000" ID="Freemind_Link_38802779" MODIFIED="1274172615378" TEXT="MainArgsConfiguration">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#990000" CREATED="1274189966047" ID="ID_1935745344" MODIFIED="1277118817351" TEXT="YamlConfiguration"/>
<node CREATED="1277118821851" ID="ID_96179827" MODIFIED="1277118823586" TEXT="..."/>
</node>
</node>
<node COLOR="#000000" CREATED="1262523811000" ID="Freemind_Link_38240458" MODIFIED="1275479177058" POSITION="right" TEXT="i18n">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1265977798567" ID="Freemind_Link_844258424" MODIFIED="1274170041130" TEXT="objectives">
<node CREATED="1265977808380" ID="Freemind_Link_30103298" MODIFIED="1289658080374" STYLE="bubble" TEXT="to handle i18n messages with tokens, extending standard jdk resource bundle"/>
<node CREATED="1265977857365" ID="Freemind_Link_1571835055" MODIFIED="1277119504285" STYLE="bubble" TEXT="to expose and manage configuration via jmx, web, web service"/>
<node CREATED="1277119466972" ID="ID_1145627297" LINK="#Freemind_Link_38802849" MODIFIED="1279693140623" STYLE="bubble" TEXT="to allow transparent use in a cluster (it uses cache stack, define your own cache policies)"/>
<node CREATED="1265977874131" ID="Freemind_Link_1998347829" LINK="#Freemind_Link_38815464" MODIFIED="1277133398274" STYLE="bubble" TEXT="It is based on file store (file://, http://, ftp://, jpa:/ ...) to access properties datas"/>
<node CREATED="1277131387990" ID="ID_225512285" MODIFIED="1279693159451" STYLE="bubble" TEXT="to introduce I18nException with error code, which extends standard Exception &amp; RuntimeException"/>
</node>
<node COLOR="#000000" CREATED="1263131048000" ID="Freemind_Link_38815915" MODIFIED="1277065046527" TEXT="implementations">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#338800" CREATED="1263131139000" ID="Freemind_Link_38815978" MODIFIED="1277126295507" TEXT="java.util.ResourceBundle.Control for properties (xml or not)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1262523873000" ID="Freemind_Link_38240477" MODIFIED="1277126296460" TEXT="java.util.ResourceBundle.Control for properties for jpa clob properties (xml or not)">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node CREATED="1274174334543" ID="ID_614757085" MODIFIED="1288858135501" POSITION="right" TEXT="context injection">
<font BOLD="true" NAME="SansSerif" SIZE="14"/>
<node CREATED="1274171238255" ID="ID_1690119130" MODIFIED="1274171272911" TEXT="objectives">
<node CREATED="1277119955735" ID="ID_964970918" MODIFIED="1277120359998" STYLE="bubble" TEXT="to define and handle a runtime context injection (as a subset of a runtime configuration)"/>
<node CREATED="1277120070953" ID="ID_899296786" MODIFIED="1279693311391" STYLE="bubble" TEXT="to allow or not dynamic runtime context configuration changes, in the instance which uses it"/>
<node CREATED="1277120260717" ID="ID_570659713" MODIFIED="1277120275436" STYLE="bubble" TEXT="the goal of the framework is not to implement an ioc layer"/>
</node>
<node CREATED="1277119858126" ID="ID_752589591" MODIFIED="1277120857338" TEXT="implementations">
<node COLOR="#338800" CREATED="1277119862938" ID="ID_1867161318" MODIFIED="1279693326579" TEXT="by default, kaleido uses aspectJ to inject classes runtime context">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#ff0000" CREATED="1277119892360" ID="ID_90170490" MODIFIED="1289658135711" TEXT="a guice ioc extension will be provided for runtime context injection"/>
<node COLOR="#ff0000" CREATED="1277120155905" ID="ID_1654330359" MODIFIED="1289658143410" TEXT="a spring ioc extension will be provided for runtime context injection "/>
<node COLOR="#ff0000" CREATED="1289658106259" ID="ID_216978226" MODIFIED="1289658151132" TEXT="a  jee6 @Inject extension will be provided for runtime context injection"/>
</node>
</node>
<node COLOR="#000000" CREATED="1262523984000" ID="Freemind_Link_38240499" MODIFIED="1274174785788" POSITION="right" TEXT="plugin">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274174788491" ID="ID_1324424775" MODIFIED="1274174791085" TEXT="objectives">
<node COLOR="#000000" CREATED="1262524574000" ID="Freemind_Link_38240772" MODIFIED="1289658240906" STYLE="bubble" TEXT="to provide user, an extensible plugin registry (interface &amp; implementation), that allow to register yours">
<font NAME="Dialog" SIZE="12"/>
</node>
<node CREATED="1277120760151" ID="ID_983704314" MODIFIED="1279693358939" STYLE="bubble" TEXT="kaleido internaly use this plugin (for file store, cache, configuration, i18n, ...)"/>
<node CREATED="1274174794647" ID="ID_213687145" MODIFIED="1277129845046" STYLE="bubble" TEXT="kaleido user / developper / constributor can add / extend / contribute with its own implem. for config, cache..."/>
</node>
<node COLOR="#000000" CREATED="1262524560000" ID="Freemind_Link_38240769" MODIFIED="1277120862651" TEXT="implementations">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#338800" CREATED="1262524231000" ID="Freemind_Link_38240567" MODIFIED="1277131908486" TEXT="use @Declare(&quot;plugin.code&quot;) on your interface">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1262524231000" ID="ID_360495755" MODIFIED="1279799807509" TEXT="use @Declare(&quot;plugin.impl.code&quot;) on your implementation">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1277131199913" ID="ID_1286775642" MODIFIED="1277131910486" TEXT="annotation processor at compile time (static), will handle @Declare interface &amp; impl. class"/>
</node>
</node>
<node CREATED="1274170698137" ID="ID_35124846" MODIFIED="1277132383827" POSITION="right" TEXT="miscellaneous">
<font BOLD="true" NAME="SansSerif" SIZE="12"/>
<node CREATED="1275479060124" ID="ID_614972537" MODIFIED="1277131849706" TEXT="aop annotation processing">
<node COLOR="#338800" CREATED="1277131473349" ID="ID_277102447" MODIFIED="1277131911268" TEXT="@NotNull on method argument or method return">
<node CREATED="1277131717128" ID="ID_827513682" MODIFIED="1277131916174" STYLE="fork" TEXT="NullPointerException replace by NotNullException with caller information"/>
<node CREATED="1277131740894" ID="ID_1332260849" MODIFIED="1277131892502" TEXT="No more need to write defensive code like if (arg == null) throw new IllegalArg..."/>
</node>
<node COLOR="#338800" CREATED="1277131479145" ID="ID_159669761" MODIFIED="1277131912268" TEXT="@NotImplemented on class or method, in order to throws NotImplementedException, instead of returning null"/>
<node COLOR="#338800" CREATED="1277131484770" ID="ID_1321466439" MODIFIED="1277132249062" TEXT="@javax.persistence.PersistenceContext works on Entitymanager field, in unmanaged container (main, war, ...)"/>
</node>
<node CREATED="1277131814643" ID="ID_1551133383" MODIFIED="1277131816581" TEXT="commons">
<node COLOR="#338800" CREATED="1274170722527" ID="ID_94883801" MODIFIED="1277132273000" TEXT="kaleido uses slf4j logger interface"/>
<node COLOR="#338800" CREATED="1274170775261" ID="ID_610748248" MODIFIED="1277132273828" TEXT="kaleido uses jee 5 / 6 standard api stack "/>
</node>
</node>
</node>
</map>
