<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1274170863354" ID="ID_144834708" MODIFIED="1274175735385" TEXT="kaleido-foundry-module">
<node COLOR="#000000" CREATED="1263131013000" ID="Freemind_Link_38815900" MODIFIED="1274170920868" POSITION="right" TEXT="security">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<icon BUILTIN="password"/>
<node CREATED="1265977758176" ID="Freemind_Link_547847240" MODIFIED="1274175983835" TEXT="objectifs">
<node COLOR="#000000" CREATED="1263131427000" ID="Freemind_Link_38816097" MODIFIED="1274175977288" TEXT="d&#xe9;finition user / group = process  / role = droit POJO pour JPA">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131505000" ID="Freemind_Link_38816124" MODIFIED="1263131506000" TEXT="droits acc&#xe8;s &#xe0; une entit&#xe9; -&gt; role">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131522000" ID="Freemind_Link_38816130" MODIFIED="1263131534000" TEXT="droits acc&#xe8;s &#xe0; un champ d&apos;une entit&#xe9; -&gt; role (ouvert par d&#xe9;faut)">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#000000" CREATED="1263130523000" ID="Freemind_Link_38815536" MODIFIED="1274170937540" POSITION="left" TEXT="rules">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<node CREATED="1265977652800" ID="Freemind_Link_1205147832" MODIFIED="1265977655690" TEXT="objectifs">
<node COLOR="#000000" CREATED="1263131638000" ID="Freemind_Link_38816224" MODIFIED="1265977700316" TEXT="d&#xe9;finir sur une entit&#xe9; (POJO), une liste de regle m&#xe9;tier (integrit&#xe9;, coherence) face &#xe0; des messages i18n">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263131801000" ID="Freemind_Link_38816295" MODIFIED="1263131901000" TEXT="level 1 int&#xe9;grite">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131810000" ID="Freemind_Link_38816298" MODIFIED="1263131994000" TEXT="level 2 coherence globale">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131838000" ID="Freemind_Link_38816304" MODIFIED="1263131873000" TEXT="level 3 coherence role user">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#000000" CREATED="1263132073000" ID="Freemind_Link_38816498" MODIFIED="1263132073000" TEXT="implementation">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263132065000" ID="Freemind_Link_38816496" MODIFIED="1263132076000" TEXT="regexp">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131654000" ID="Freemind_Link_38816226" MODIFIED="1265977640331" TEXT="bean validator (&#xe0; tester)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132059000" ID="Freemind_Link_38816495" MODIFIED="1263132112000" TEXT="antlr (langage &#xe0; d&#xe9;finir)">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132048000" ID="Freemind_Link_38816491" MODIFIED="1263132131000" TEXT="groovy script (scripting)">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#000000" CREATED="1263132815000" ID="Freemind_Link_38816913" MODIFIED="1274170948477" POSITION="right" TEXT="messaging">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<icon BUILTIN="korn"/>
<node COLOR="#000000" CREATED="1263132857000" ID="Freemind_Link_38816929" MODIFIED="1274170951431" TEXT="implementation">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263132871000" ID="Freemind_Link_38816934" MODIFIED="1263132871000" TEXT="JMS">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132879000" ID="Freemind_Link_38816938" MODIFIED="1263132884000" TEXT="Tibco RVD">
<font NAME="Dialog" SIZE="12"/>
</node>
<node CREATED="1274170952493" ID="ID_1158166524" MODIFIED="1274170954306" TEXT="JMX"/>
</node>
<node CREATED="1274175683979" ID="ID_1750028632" MODIFIED="1274175699823" TEXT="le message bean doit etre annot&#xe9; / able  JPA"/>
</node>
<node COLOR="#000000" CREATED="1263130560000" ID="Freemind_Link_38815566" MODIFIED="1263132805000" POSITION="left" TEXT="report">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
</node>
<node COLOR="#000000" CREATED="1263130576000" ID="Freemind_Link_38815586" MODIFIED="1263132892000" POSITION="left" TEXT="mailer">
<font BOLD="true" NAME="Dialog" SIZE="14"/>
<icon BUILTIN="Mail"/>
<node COLOR="#000000" CREATED="1263132895000" ID="Freemind_Link_38816945" MODIFIED="1263132895000" TEXT="implementation">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263132901000" ID="Freemind_Link_38816947" MODIFIED="1263132906000" TEXT="smtp local">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132913000" ID="Freemind_Link_38816953" MODIFIED="1263132913000" TEXT="smtp jndi">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132931000" ID="Freemind_Link_38816957" MODIFIED="1263132931000" TEXT="smtp ejb jms">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node CREATED="1274174501854" ID="ID_726454568" MODIFIED="1274174504776" POSITION="right" TEXT="admin-ui">
<node COLOR="#000000" CREATED="1263131162000" ID="Freemind_Link_38815990" MODIFIED="1263131162000" TEXT="meta model">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263131262000" ID="Freemind_Link_38816051" MODIFIED="1263131262000" TEXT="cr&#xe9;er / maj / remove entit&#xe9;">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131280000" ID="Freemind_Link_38816057" MODIFIED="1263131280000" TEXT="ajouter / maj / d&#xe9;sactiver fields d&apos;une entit&#xe9;">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131332000" ID="Freemind_Link_38816072" MODIFIED="1263131332000" TEXT="fournir un outil de filtre (sql ou cache) unifi&#xe9;">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263131358000" ID="Freemind_Link_38816081" MODIFIED="1263131358000" TEXT="exposer une entite (CRUD Service) via web service">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
<node CREATED="1274175789009" ID="ID_176394367" MODIFIED="1274175800603" TEXT="security part (mapping user / credential)"/>
</node>
<node CREATED="1274175736447" ID="ID_1272158000" MODIFIED="1274175927054" POSITION="left" TEXT="model">
<font BOLD="true" NAME="SansSerif" SIZE="14"/>
<node CREATED="1274175741838" ID="ID_365479456" MODIFIED="1274175745525" TEXT="meta model jpa 2 ?"/>
<node COLOR="#000000" CREATED="1263132223000" ID="Freemind_Link_38816597" MODIFIED="1274175781337" TEXT="grid-ui">
<font NAME="Dialog" SIZE="13"/>
<node COLOR="#000000" CREATED="1263132251000" ID="Freemind_Link_38816599" MODIFIED="1263132251000" TEXT="descripteur d&apos;une gris / liste">
<font NAME="Dialog" SIZE="12"/>
<node COLOR="#000000" CREATED="1263132257000" ID="Freemind_Link_38816600" MODIFIED="1263132257000" TEXT="filtre">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132264000" ID="Freemind_Link_38816601" MODIFIED="1263132264000" TEXT="pagination">
<font NAME="Dialog" SIZE="12"/>
</node>
<node COLOR="#000000" CREATED="1263132269000" ID="Freemind_Link_38816602" MODIFIED="1263132269000" TEXT="columns">
<font NAME="Dialog" SIZE="12"/>
</node>
</node>
</node>
<node CREATED="1274175932398" ID="ID_495236490" MODIFIED="1274175938648" TEXT="model dans cache distribu&#xe9;"/>
</node>
</node>
</map>
