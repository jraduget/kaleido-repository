CREATE TABLE CONFIGURATION_PROPERTIES (CONFIGURATION_ID BIGINT NOT NULL, PROPERTY_ID BIGINT NOT NULL, PRIMARY KEY (CONFIGURATION_ID, PROPERTY_ID))
CREATE TABLE CONFIGURATION (ID BIGINT NOT NULL, STORABLE SMALLINT DEFAULT 0, LOADED SMALLINT DEFAULT 0, UPDATEABLE SMALLINT DEFAULT 0, DESCRIPTION VARCHAR(255), NAME VARCHAR(255) UNIQUE, URI VARCHAR(255) UNIQUE, VERSION INTEGER, LABELS VARCHAR(255), PRIMARY KEY (ID))
CREATE INDEX IX_CONFIGURATION_CONFIGURATION0 ON CONFIGURATION (NAME)
CREATE INDEX IX_CONFIGURATION_CONFIGURATION1 ON CONFIGURATION (URI)


CREATE TABLE I18N_ENTRY (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, ENABLED SMALLINT DEFAULT 0, DESCRIPTION VARCHAR(255), CODE VARCHAR(255), TYPE INTEGER, LASTUSED TIMESTAMP, VERSION INTEGER, GROUPE_CODE VARCHAR(255), PRIMARY KEY (ID))
CREATE INDEX IX_I18N_ENTRY_UNQ_I18N_ENTRY_0 ON I18N_ENTRY (CODE, GROUP_CODE)
CREATE TABLE I18N_LANGUAGE (ID INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL, CONTENT VARCHAR(255), LOCALE_ID VARCHAR(255), ISOLANGUAGE VARCHAR(255), VERSION INTEGER, MESSAGE_ID BIGINT, PRIMARY KEY (ID))
CREATE INDEX IX_I18N_LANGUAGE_I18N_LANGUAGE0 ON I18N_LANGUAGE (MESSAGE_ID, LOCALE)
CREATE TABLE FILESTORE (URI VARCHAR(255) NOT NULL, CONTENT BLOB(255), CREATIONDATE TIMESTAMP NOT NULL, CONTENTSIZE INTEGER, UPDATEDDATE TIMESTAMP, NAME VARCHAR(255), PATH VARCHAR(255), CONTENTMIMETYPE VARCHAR(255), VERSION INTEGER, PRIMARY KEY (URI))
CREATE TABLE CONFIGURATION_PROPERTY (ID BIGINT NOT NULL, DESCRIPTION VARCHAR(255), NAME VARCHAR(255), VALUE VARCHAR(255), TYPE VARCHAR(255), VERSION INTEGER, LABELS VARCHAR(255), PRIMARY KEY (ID))
CREATE TABLE I18N_ENTRY_I18N_LANGUAGE (I18nMessage_ID BIGINT NOT NULL, messageLanguages_ID INTEGER NOT NULL, PRIMARY KEY (I18nMessage_ID, messageLanguages_ID))
CREATE TABLE I18N_GROUP (CODE VARCHAR(255) NOT NULL, VERSION INTEGER, DESCRIPTION_ID BIGINT, PARENT_CODE VARCHAR(255), PRIMARY KEY (CODE))
ALTER TABLE CONFIGURATION ADD CONSTRAINT CONFIGURATION0 UNIQUE (NAME)
ALTER TABLE CONFIGURATION ADD CONSTRAINT CONFIGURATION1 UNIQUE (URI)
ALTER TABLE I18N_ENTRY ADD CONSTRAINT UNQ_I18N_ENTRY_0 UNIQUE (CODE, GROUP_CODE)
ALTER TABLE I18N_LANGUAGE ADD CONSTRAINT I18N_LANGUAGE0 UNIQUE (MESSAGE_ID, LOCALE)
ALTER TABLE CONFIGURATION_PROPERTIES ADD CONSTRAINT CNFGRTNPRPRPRPRTYD FOREIGN KEY (PROPERTY_ID) REFERENCES CONFIGURATION_PROPERTY (ID)
ALTER TABLE CONFIGURATION_PROPERTIES ADD CONSTRAINT CNFGRTNPRPCNFGRTND FOREIGN KEY (CONFIGURATION_ID) REFERENCES CONFIGURATION (ID)
ALTER TABLE I18N_ENTRY ADD CONSTRAINT 18NENTRYGROUPECODE FOREIGN KEY (GROUPE_CODE) REFERENCES I18N_GROUP (CODE)
ALTER TABLE I18N_LANGUAGE ADD CONSTRAINT 18NLANGUAGEMSSGEID FOREIGN KEY (MESSAGE_ID) REFERENCES I18N_ENTRY (ID)
ALTER TABLE I18N_ENTRY_I18N_LANGUAGE ADD CONSTRAINT 18NNTRY18N18nMssgD FOREIGN KEY (I18nMessage_ID) REFERENCES I18N_ENTRY (ID)
ALTER TABLE I18N_ENTRY_I18N_LANGUAGE ADD CONSTRAINT 18NNTRY1mssgLnggsD FOREIGN KEY (messageLanguages_ID) REFERENCES I18N_LANGUAGE (ID)
ALTER TABLE I18N_GROUP ADD CONSTRAINT 18NGROUPPARENTCODE FOREIGN KEY (PARENT_CODE) REFERENCES I18N_GROUP (CODE)
ALTER TABLE I18N_GROUP ADD CONSTRAINT 18NGRUPDSCRPTIONID FOREIGN KEY (DESCRIPTION_ID) REFERENCES I18N_ENTRY (ID)
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(15), PRIMARY KEY (SEQ_NAME))
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0)