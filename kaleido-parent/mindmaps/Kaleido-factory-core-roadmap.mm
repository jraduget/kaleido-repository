<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1262523247000" ID="Freemind_Link_38240127" MODIFIED="1275478972032" TEXT="Kaleido-foundry-core">
<font BOLD="true" ITALIC="true" NAME="Dialog" SIZE="14"/>
<node COLOR="#000000" CREATED="1263130504000" ID="Freemind_Link_38815464" MODIFIED="1274170195628" POSITION="left" TEXT="store">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274170058864" ID="ID_1594601089" MODIFIED="1274171444518" TEXT="objectives">
<node CREATED="1274170066317" ID="ID_1128149001" MODIFIED="1274172659315" STYLE="bubble" TEXT="provide an uniform access to a small resource file"/>
<node CREATED="1274170094129" ID="ID_1172682028" MODIFIED="1274172659315" STYLE="bubble" TEXT="support different back end to access file :"/>
<node CREATED="1274170113535" ID="ID_715042817" MODIFIED="1274172659315" STYLE="bubble" TEXT="file system, ftp, http, clob, ..."/>
</node>
<node CREATED="1265977562627" ID="Freemind_Link_72346299" MODIFIED="1274170270892" TEXT="implementation">
<node COLOR="#338800" CREATED="1274170207846" HGAP="18" ID="ID_1485872953" MODIFIED="1274172597550" TEXT="ClasspathResourceStore"/>
<node COLOR="#338800" CREATED="1274170224096" ID="ID_1332461641" MODIFIED="1274172597550" TEXT="FileSystemResourceStore"/>
<node COLOR="#338800" CREATED="1274170233705" ID="ID_977496062" MODIFIED="1274172597550" TEXT="FtpResourceStore"/>
<node COLOR="#338800" CREATED="1274170241752" ID="ID_1332115076" MODIFIED="1274172597550" TEXT="HttpResourceStore"/>
<node COLOR="#338800" CREATED="1274170252299" ID="ID_1390417665" MODIFIED="1274172597534" TEXT="JpaResourceStore"/>
<node COLOR="#338800" CREATED="1274170272220" ID="ID_204522737" MODIFIED="1274172597534" TEXT="MemoryResourceStore"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114329000" ID="Freemind_Link_38802849" MODIFIED="1274174764600" POSITION="left" TEXT="cache">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274171932980" ID="ID_1061235509" MODIFIED="1274172088244" TEXT="objectives">
<node CREATED="1274171948324" ID="ID_928887048" MODIFIED="1274172643815" STYLE="bubble" TEXT="provides an unique and simple interface to access cache datas"/>
<node CREATED="1274171972621" ID="ID_239610198" MODIFIED="1274172643831" STYLE="bubble" TEXT="provides implementations bridges (open source or commercial)"/>
<node CREATED="1274172294257" ID="ID_613471733" MODIFIED="1274172643831" STYLE="bubble" TEXT="you are no more dependent of an intrusive implementation"/>
<node CREATED="1274172260820" ID="ID_167134996" MODIFIED="1274172643831" STYLE="bubble" TEXT="a transparent use - cache can be local or distributed via external configuration"/>
</node>
<node CREATED="1265890816941" ID="Freemind_Link_235149795" MODIFIED="1274172515848" TEXT="implementation">
<node COLOR="#338800" CREATED="1274172539707" ID="ID_466824154" MODIFIED="1274172585457" TEXT="default local"/>
<node COLOR="#338800" CREATED="1274172480723" ID="ID_1768588229" MODIFIED="1274172585472" TEXT="ehcache 1.x to 2.x"/>
<node COLOR="#338800" CREATED="1274172495051" ID="ID_1671494473" MODIFIED="1274172585472" TEXT="jboss cache 3.X"/>
<node COLOR="#338800" CREATED="1274172516754" ID="ID_1142104684" MODIFIED="1274172585472" TEXT="jboss infinispan 4.x"/>
<node COLOR="#338800" CREATED="1274172464146" ID="ID_1719665764" MODIFIED="1274172585472" TEXT="coherence 3.x"/>
<node CREATED="1274172562332" ID="ID_1348216642" MODIFIED="1274172566019" TEXT="gigaspace"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114081000" ID="Freemind_Link_38802741" MODIFIED="1274169923162" POSITION="left" TEXT="configuration">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1265977922834" ID="Freemind_Link_1917486617" MODIFIED="1274171775201" TEXT="objectives">
<node CREATED="1265977931741" ID="Freemind_Link_1040325902" MODIFIED="1274173951236" STYLE="bubble" TEXT="provide unified runtime configuration access">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1265977969866" ID="Freemind_Link_668602541" MODIFIED="1274173951220" STYLE="bubble" TEXT="manageable by a gui (jmx or web)">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1274171777061" ID="ID_611464567" MODIFIED="1274173951220" STYLE="bubble" TEXT="transparent use in a clustered - use cache stack">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node CREATED="1265977948585" ID="Freemind_Link_619061248" MODIFIED="1274173951220" STYLE="bubble" TEXT="multiple resource store stack to access datas">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263303841000" ID="Freemind_Link_38992791" MODIFIED="1263303841000" TEXT="use case">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263303868000" ID="Freemind_Link_38992852" MODIFIED="1264090558000" TEXT="@Context(&quot;resource&quot;)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263130371000" ID="Freemind_Link_38815307" MODIFIED="1263303875000" TEXT="heritage &amp; hierarchique">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132772000" ID="Freemind_Link_38816897" MODIFIED="1263303847000" TEXT="cache distribu&#xe9; ou non">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263114307000" ID="Freemind_Link_38802815" MODIFIED="1274189964765" TEXT="implementation">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#338800" CREATED="1263114284000" ID="Freemind_Link_38802779" MODIFIED="1274172615378" TEXT="MainArgsConfiguration">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1263114098000" ID="Freemind_Link_38802743" MODIFIED="1274172615378" TEXT="JavaSystemConfiguration">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1263133328000" ID="Freemind_Link_38817175" MODIFIED="1274172615378" TEXT="PropertiesConfiguration (default or xml)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#338800" CREATED="1274169638025" ID="ID_1628091349" MODIFIED="1274172615378" TEXT="XmlConfiguration"/>
<node CREATED="1274189966047" ID="ID_1935745344" MODIFIED="1274189977000" TEXT="YamlConfiguration"/>
</node>
</node>
<node COLOR="#000000" CREATED="1262523811000" ID="Freemind_Link_38240458" MODIFIED="1275479177058" POSITION="right" TEXT="i18n">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1265977798567" ID="Freemind_Link_844258424" MODIFIED="1274170041130" TEXT="objectives">
<node CREATED="1265977808380" ID="Freemind_Link_30103298" MODIFIED="1274172676690" STYLE="bubble" TEXT="handle i18n messages with token"/>
<node COLOR="#000000" CREATED="1262523836000" ID="Freemind_Link_38240462" MODIFIED="1274174594743" STYLE="bubble" TEXT="extends standard resource bundle">
<font NAME="Dialog" SIZE="12"/>
</node>
<node CREATED="1265977857365" ID="Freemind_Link_1571835055" MODIFIED="1274172676690" STYLE="bubble" TEXT="reloadable and distributed if needed"/>
<node CREATED="1265977874131" ID="Freemind_Link_1998347829" MODIFIED="1274174684883" STYLE="bubble" TEXT="based on resource store (file://, http://, ftp://, jpa:/ ...)"/>
</node>
<node COLOR="#000000" CREATED="1263303780000" ID="Freemind_Link_38992702" MODIFIED="1274175113455" TEXT="use case">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263113959000" ID="Freemind_Link_38802684" MODIFIED="1274175141721" TEXT="@Context(name=&quot;context.id&quot;, &lt;configId=&quot;config.id&quot;&gt;) private I18nMessages messages;">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263113959000" ID="ID_52603171" MODIFIED="1274175213892" TEXT="@Context(uri=&quot;http://${config.host}/myMessages&quot;&gt;, &lt;configId=&quot;config.id&quot;&gt;) private I18nMessages messages;">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
<node COLOR="#000000" CREATED="1263131048000" ID="Freemind_Link_38815915" MODIFIED="1263131048000" TEXT="implementation">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1262523862000" ID="Freemind_Link_38240475" MODIFIED="1263131134000" TEXT="store base de donn&#xe9;es CRUD">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131139000" ID="Freemind_Link_38815978" MODIFIED="1263131141000" TEXT="store base de donn&#xe9;es JPA">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1262523873000" ID="Freemind_Link_38240477" MODIFIED="1263131071000" TEXT="cr&#xe9;er un store cache distribu&#xe9;">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node CREATED="1274174334543" ID="ID_614757085" MODIFIED="1275479048968" POSITION="right" TEXT="ioc / context">
<font BOLD="true" NAME="SansSerif" SIZE="14"/>
<node CREATED="1274171238255" ID="ID_1690119130" MODIFIED="1274171272911" TEXT="objectives">
<node CREATED="1268217620290" ID="Freemind_Link_140343983" MODIFIED="1274172685346" STYLE="bubble" TEXT="handle configuration context injection"/>
<node CREATED="1274171274176" ID="ID_1739026425" MODIFIED="1274172685346" STYLE="bubble" TEXT="provide a reload mecanism, when configuration changed at runtime"/>
</node>
<node CREATED="1274174372231" ID="ID_308600192" MODIFIED="1274174379996" TEXT="use case">
<node COLOR="#000000" CREATED="1262524250000" ID="Freemind_Link_38240577" MODIFIED="1262524563000" TEXT="@ResourcePlugin(name=&quot;&quot;, type=&quot;&quot;) (use case)">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#000000" CREATED="1262523984000" ID="Freemind_Link_38240499" MODIFIED="1274174785788" POSITION="right" TEXT="plugin">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1274174788491" ID="ID_1324424775" MODIFIED="1274174791085" TEXT="objectives">
<node COLOR="#000000" CREATED="1262524574000" ID="Freemind_Link_38240772" MODIFIED="1274174942676" TEXT="provide a plugin registry">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1262524207000" ID="Freemind_Link_38240558" MODIFIED="1274174982035" TEXT="all core is defined via plugin : i18 , cache, configuration, store, ..">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
<node CREATED="1274174794647" ID="ID_213687145" MODIFIED="1274175008004" TEXT="developper can add / extends its own implementations for config, cache..."/>
</node>
<node COLOR="#000000" CREATED="1262524560000" ID="Freemind_Link_38240769" MODIFIED="1274174385340" TEXT="use case">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#006600" CREATED="1262524231000" ID="Freemind_Link_38240567" MODIFIED="1274174906286" TEXT="@DeclarePlugin (&quot;plugin.code&quot;) on interface and implementation">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#000000" CREATED="1263113938000" ID="Freemind_Link_38802679" MODIFIED="1274175582528" POSITION="right" TEXT="admin-ui">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node COLOR="#000000" CREATED="1263113999000" ID="Freemind_Link_38802700" MODIFIED="1274174539885" TEXT="i18n manager">
<font NAME="Dialog" SIZE="12"/>
<node CREATED="1274174541072" ID="ID_652533825" MODIFIED="1274174544010" TEXT="gwt &amp; jmx"/>
</node>
<node COLOR="#000000" CREATED="1263114018000" ID="Freemind_Link_38802732" MODIFIED="1274174451151" TEXT="log gui manager">
<font NAME="Dialog" SIZE="12"/>
<node CREATED="1274174452448" ID="ID_1122133381" MODIFIED="1274174469354" TEXT="gwt &amp; jmx"/>
<node CREATED="1274174428870" ID="ID_659372128" MODIFIED="1274174436402" TEXT="tail"/>
</node>
<node COLOR="#000000" CREATED="1263114071000" ID="Freemind_Link_38802739" MODIFIED="1263114071000" TEXT="configuration manager &amp; reload">
<font NAME="Dialog" SIZE="12"/>
</node>
<node CREATED="1274175584418" ID="ID_966015100" MODIFIED="1274175589606" TEXT="simple cache statistic"/>
</node>
<node CREATED="1274170698137" ID="ID_35124846" MODIFIED="1275479058593" POSITION="right" TEXT="miscellaneous">
<node CREATED="1274170722527" ID="ID_94883801" MODIFIED="1274170729027" TEXT="use slf4j logger"/>
<node CREATED="1274170733761" ID="ID_1796711050" MODIFIED="1274170767870" TEXT="no ioc implementation, connector with guice, spring prefered"/>
<node CREATED="1274170775261" ID="ID_610748248" MODIFIED="1274170828885" TEXT="use jee 5 / 6 standard stack"/>
<node CREATED="1274175621527" ID="ID_427415095" MODIFIED="1274175631058" TEXT="incoming in next roadmap">
<node CREATED="1274175631746" ID="ID_394427967" MODIFIED="1274175643183" TEXT="messaging (jms, rdv, jmx...)"/>
<node CREATED="1274175645027" ID="ID_432630255" MODIFIED="1274175661730" TEXT="mailler (local, jndi, ejb mdb, jms)"/>
</node>
<node CREATED="1275479060124" ID="ID_614972537" MODIFIED="1275479108201" TEXT="Aop annotation : @NotNull, @NotYetImplemented, @Inject in non managed env."/>
</node>
</node>
</map>
