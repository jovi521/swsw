CREATE TABLE ACT_GE_PROPERTY
(
    NAME_  VARCHAR(64),
    VALUE_ VARCHAR(300),
    REV_   INTEGER,
    PRIMARY KEY (NAME_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
INSERT INTO ACT_GE_PROPERTY
VALUES ('schema.version', '7.1.0-M6', 1);
INSERT INTO ACT_GE_PROPERTY
VALUES ('schema.history', 'create(7.1.0-M6)', 1);
INSERT INTO ACT_GE_PROPERTY
VALUES ('next.dbid', '1', 1);
CREATE TABLE ACT_GE_BYTEARRAY
(
    ID_            VARCHAR(64),
    REV_           INTEGER,
    NAME_          VARCHAR(255),
    DEPLOYMENT_ID_ VARCHAR(64),
    BYTES_         LONGBLOB,
    GENERATED_     TINYINT,
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RE_DEPLOYMENT
(
    ID_                      VARCHAR(64),
    NAME_                    VARCHAR(255),
    CATEGORY_                VARCHAR(255),
    KEY_                     VARCHAR(255),
    TENANT_ID_               VARCHAR(255) DEFAULT '',
    DEPLOY_TIME_             TIMESTAMP(3) NULL,
    ENGINE_VERSION_          VARCHAR(255),
    VERSION_                 INTEGER      DEFAULT 1,
    PROJECT_RELEASE_VERSION_ VARCHAR(255),
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RE_MODEL
(
    ID_                           VARCHAR(64)  NOT NULL,
    REV_                          INTEGER,
    NAME_                         VARCHAR(255),
    KEY_                          VARCHAR(255),
    CATEGORY_                     VARCHAR(255),
    CREATE_TIME_                  TIMESTAMP(3) NULL,
    LAST_UPDATE_TIME_             TIMESTAMP(3) NULL,
    VERSION_                      INTEGER,
    META_INFO_                    VARCHAR(4000),
    DEPLOYMENT_ID_                VARCHAR(64),
    EDITOR_SOURCE_VALUE_ID_       VARCHAR(64),
    EDITOR_SOURCE_EXTRA_VALUE_ID_ VARCHAR(64),
    TENANT_ID_                    VARCHAR(255) DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_EXECUTION
(
    ID_                   VARCHAR(64),
    REV_                  INTEGER,
    PROC_INST_ID_         VARCHAR(64),
    BUSINESS_KEY_         VARCHAR(255),
    PARENT_ID_            VARCHAR(64),
    PROC_DEF_ID_          VARCHAR(64),
    SUPER_EXEC_           VARCHAR(64),
    ROOT_PROC_INST_ID_    VARCHAR(64),
    ACT_ID_               VARCHAR(255),
    IS_ACTIVE_            TINYINT,
    IS_CONCURRENT_        TINYINT,
    IS_SCOPE_             TINYINT,
    IS_EVENT_SCOPE_       TINYINT,
    IS_MI_ROOT_           TINYINT,
    SUSPENSION_STATE_     INTEGER,
    CACHED_ENT_STATE_     INTEGER,
    TENANT_ID_            VARCHAR(255) DEFAULT '',
    NAME_                 VARCHAR(255),
    START_TIME_           datetime(3),
    START_USER_ID_        VARCHAR(255),
    LOCK_TIME_            TIMESTAMP(3) NULL,
    IS_COUNT_ENABLED_     TINYINT,
    EVT_SUBSCR_COUNT_     INTEGER,
    TASK_COUNT_           INTEGER,
    JOB_COUNT_            INTEGER,
    TIMER_JOB_COUNT_      INTEGER,
    SUSP_JOB_COUNT_       INTEGER,
    DEADLETTER_JOB_COUNT_ INTEGER,
    VAR_COUNT_            INTEGER,
    ID_LINK_COUNT_        INTEGER,
    APP_VERSION_          INTEGER,
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_JOB
(
    ID_                  VARCHAR(64)  NOT NULL,
    REV_                 INTEGER,
    TYPE_                VARCHAR(255) NOT NULL,
    LOCK_EXP_TIME_       TIMESTAMP(3) NULL,
    LOCK_OWNER_          VARCHAR(255),
    EXCLUSIVE_           boolean,
    EXECUTION_ID_        VARCHAR(64),
    PROCESS_INSTANCE_ID_ VARCHAR(64),
    PROC_DEF_ID_         VARCHAR(64),
    RETRIES_             INTEGER,
    EXCEPTION_STACK_ID_  VARCHAR(64),
    EXCEPTION_MSG_       VARCHAR(4000),
    DUEDATE_             TIMESTAMP(3) NULL,
    REPEAT_              VARCHAR(255),
    HANDLER_TYPE_        VARCHAR(255),
    HANDLER_CFG_         VARCHAR(4000),
    TENANT_ID_           VARCHAR(255) DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_TIMER_JOB
(
    ID_                  VARCHAR(64)  NOT NULL,
    REV_                 INTEGER,
    TYPE_                VARCHAR(255) NOT NULL,
    LOCK_EXP_TIME_       TIMESTAMP(3) NULL,
    LOCK_OWNER_          VARCHAR(255),
    EXCLUSIVE_           boolean,
    EXECUTION_ID_        VARCHAR(64),
    PROCESS_INSTANCE_ID_ VARCHAR(64),
    PROC_DEF_ID_         VARCHAR(64),
    RETRIES_             INTEGER,
    EXCEPTION_STACK_ID_  VARCHAR(64),
    EXCEPTION_MSG_       VARCHAR(4000),
    DUEDATE_             TIMESTAMP(3) NULL,
    REPEAT_              VARCHAR(255),
    HANDLER_TYPE_        VARCHAR(255),
    HANDLER_CFG_         VARCHAR(4000),
    TENANT_ID_           VARCHAR(255) DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_SUSPENDED_JOB
(
    ID_                  VARCHAR(64)  NOT NULL,
    REV_                 INTEGER,
    TYPE_                VARCHAR(255) NOT NULL,
    EXCLUSIVE_           boolean,
    EXECUTION_ID_        VARCHAR(64),
    PROCESS_INSTANCE_ID_ VARCHAR(64),
    PROC_DEF_ID_         VARCHAR(64),
    RETRIES_             INTEGER,
    EXCEPTION_STACK_ID_  VARCHAR(64),
    EXCEPTION_MSG_       VARCHAR(4000),
    DUEDATE_             TIMESTAMP(3) NULL,
    REPEAT_              VARCHAR(255),
    HANDLER_TYPE_        VARCHAR(255),
    HANDLER_CFG_         VARCHAR(4000),
    TENANT_ID_           VARCHAR(255) DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_DEADLETTER_JOB
(
    ID_                  VARCHAR(64)  NOT NULL,
    REV_                 INTEGER,
    TYPE_                VARCHAR(255) NOT NULL,
    EXCLUSIVE_           boolean,
    EXECUTION_ID_        VARCHAR(64),
    PROCESS_INSTANCE_ID_ VARCHAR(64),
    PROC_DEF_ID_         VARCHAR(64),
    EXCEPTION_STACK_ID_  VARCHAR(64),
    EXCEPTION_MSG_       VARCHAR(4000),
    DUEDATE_             TIMESTAMP(3) NULL,
    REPEAT_              VARCHAR(255),
    HANDLER_TYPE_        VARCHAR(255),
    HANDLER_CFG_         VARCHAR(4000),
    TENANT_ID_           VARCHAR(255) DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RE_PROCDEF
(
    ID_                     VARCHAR(64)  NOT NULL,
    REV_                    INTEGER,
    CATEGORY_               VARCHAR(255),
    NAME_                   VARCHAR(255),
    KEY_                    VARCHAR(255) NOT NULL,
    VERSION_                INTEGER      NOT NULL,
    DEPLOYMENT_ID_          VARCHAR(64),
    RESOURCE_NAME_          VARCHAR(4000),
    DGRM_RESOURCE_NAME_     VARCHAR(4000),
    DESCRIPTION_            VARCHAR(4000),
    HAS_START_FORM_KEY_     TINYINT,
    HAS_GRAPHICAL_NOTATION_ TINYINT,
    SUSPENSION_STATE_       INTEGER,
    TENANT_ID_              VARCHAR(255) DEFAULT '',
    ENGINE_VERSION_         VARCHAR(255),
    APP_VERSION_            INTEGER,
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_TASK
(
    ID_               VARCHAR(64),
    REV_              INTEGER,
    EXECUTION_ID_     VARCHAR(64),
    PROC_INST_ID_     VARCHAR(64),
    PROC_DEF_ID_      VARCHAR(64),
    NAME_             VARCHAR(255),
    BUSINESS_KEY_     VARCHAR(255),
    PARENT_TASK_ID_   VARCHAR(64),
    DESCRIPTION_      VARCHAR(4000),
    TASK_DEF_KEY_     VARCHAR(255),
    OWNER_            VARCHAR(255),
    ASSIGNEE_         VARCHAR(255),
    DELEGATION_       VARCHAR(64),
    PRIORITY_         INTEGER,
    CREATE_TIME_      TIMESTAMP(3) NULL,
    DUE_DATE_         datetime(3),
    CATEGORY_         VARCHAR(255),
    SUSPENSION_STATE_ INTEGER,
    TENANT_ID_        VARCHAR(255) DEFAULT '',
    FORM_KEY_         VARCHAR(255),
    CLAIM_TIME_       datetime(3),
    APP_VERSION_      INTEGER,
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_IDENTITYLINK
(
    ID_           VARCHAR(64),
    REV_          INTEGER,
    GROUP_ID_     VARCHAR(255),
    TYPE_         VARCHAR(255),
    USER_ID_      VARCHAR(255),
    TASK_ID_      VARCHAR(64),
    PROC_INST_ID_ VARCHAR(64),
    PROC_DEF_ID_  VARCHAR(64),
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_VARIABLE
(
    ID_           VARCHAR(64)  NOT NULL,
    REV_          INTEGER,
    TYPE_         VARCHAR(255) NOT NULL,
    NAME_         VARCHAR(255) NOT NULL,
    EXECUTION_ID_ VARCHAR(64),
    PROC_INST_ID_ VARCHAR(64),
    TASK_ID_      VARCHAR(64),
    BYTEARRAY_ID_ VARCHAR(64),
    DOUBLE_       DOUBLE,
    LONG_         BIGINT,
    TEXT_         VARCHAR(4000),
    TEXT2_        VARCHAR(4000),
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_EVENT_SUBSCR
(
    ID_            VARCHAR(64)  NOT NULL,
    REV_           INTEGER,
    EVENT_TYPE_    VARCHAR(255) NOT NULL,
    EVENT_NAME_    VARCHAR(255),
    EXECUTION_ID_  VARCHAR(64),
    PROC_INST_ID_  VARCHAR(64),
    ACTIVITY_ID_   VARCHAR(64),
    CONFIGURATION_ VARCHAR(255),
    CREATED_       TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PROC_DEF_ID_   VARCHAR(64),
    TENANT_ID_     VARCHAR(255)          DEFAULT '',
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_EVT_LOG
(
    LOG_NR_       BIGINT auto_increment,
    TYPE_         VARCHAR(64),
    PROC_DEF_ID_  VARCHAR(64),
    PROC_INST_ID_ VARCHAR(64),
    EXECUTION_ID_ VARCHAR(64),
    TASK_ID_      VARCHAR(64),
    TIME_STAMP_   TIMESTAMP(3) NOT NULL,
    USER_ID_      VARCHAR(255),
    DATA_         LONGBLOB,
    LOCK_OWNER_   VARCHAR(255),
    LOCK_TIME_    TIMESTAMP(3) NULL,
    IS_PROCESSED_ TINYINT DEFAULT 0,
    PRIMARY KEY (LOG_NR_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_PROCDEF_INFO
(
    ID_           VARCHAR(64) NOT NULL,
    PROC_DEF_ID_  VARCHAR(64) NOT NULL,
    REV_          INTEGER,
    INFO_JSON_ID_ VARCHAR(64),
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE TABLE ACT_RU_INTEGRATION
(
    ID_                  VARCHAR(64) NOT NULL,
    EXECUTION_ID_        VARCHAR(64),
    PROCESS_INSTANCE_ID_ VARCHAR(64),
    PROC_DEF_ID_         VARCHAR(64),
    FLOW_NODE_ID_        VARCHAR(64),
    CREATED_DATE_        TIMESTAMP(3),
    PRIMARY KEY (ID_)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_general_ci;
CREATE INDEX ACT_IDX_EXEC_BUSKEY ON ACT_RU_EXECUTION (BUSINESS_KEY_);
CREATE INDEX ACT_IDC_EXEC_ROOT ON ACT_RU_EXECUTION (ROOT_PROC_INST_ID_);
CREATE INDEX ACT_IDX_TASK_CREATE ON ACT_RU_TASK (CREATE_TIME_);
CREATE INDEX ACT_IDX_IDENT_LNK_USER ON ACT_RU_IDENTITYLINK (USER_ID_);
CREATE INDEX ACT_IDX_IDENT_LNK_GROUP ON ACT_RU_IDENTITYLINK (GROUP_ID_);
CREATE INDEX ACT_IDX_EVENT_SUBSCR_CONFIG_ ON ACT_RU_EVENT_SUBSCR (CONFIGURATION_);
CREATE INDEX ACT_IDX_VARIABLE_TASK_ID ON ACT_RU_VARIABLE (TASK_ID_);
CREATE INDEX ACT_IDX_ATHRZ_PROCEDEF ON ACT_RU_IDENTITYLINK (PROC_DEF_ID_);
CREATE INDEX ACT_IDX_INFO_PROCDEF ON ACT_PROCDEF_INFO (PROC_DEF_ID_);
ALTER TABLE ACT_GE_BYTEARRAY
    ADD CONSTRAINT ACT_FK_BYTEARR_DEPL FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES ACT_RE_DEPLOYMENT (ID_);
ALTER TABLE ACT_RE_PROCDEF
    ADD CONSTRAINT ACT_UNIQ_PROCDEF UNIQUE (KEY_, VERSION_, TENANT_ID_);
ALTER TABLE ACT_RU_EXECUTION
    ADD CONSTRAINT ACT_FK_EXE_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES ACT_RU_EXECUTION (ID_) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE ACT_RU_EXECUTION
    ADD CONSTRAINT ACT_FK_EXE_PARENT FOREIGN KEY (PARENT_ID_) REFERENCES ACT_RU_EXECUTION (ID_) ON DELETE CASCADE;
ALTER TABLE ACT_RU_EXECUTION
    ADD CONSTRAINT ACT_FK_EXE_SUPER FOREIGN KEY (SUPER_EXEC_) REFERENCES ACT_RU_EXECUTION (ID_) ON DELETE CASCADE;
ALTER TABLE ACT_RU_EXECUTION
    ADD CONSTRAINT ACT_FK_EXE_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_IDENTITYLINK
    ADD CONSTRAINT ACT_FK_TSKASS_TASK FOREIGN KEY (TASK_ID_) REFERENCES ACT_RU_TASK (ID_);
ALTER TABLE ACT_RU_IDENTITYLINK
    ADD CONSTRAINT ACT_FK_ATHRZ_PROCEDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_IDENTITYLINK
    ADD CONSTRAINT ACT_FK_IDL_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_TASK
    ADD CONSTRAINT ACT_FK_TASK_EXE FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_TASK
    ADD CONSTRAINT ACT_FK_TASK_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_TASK
    ADD CONSTRAINT ACT_FK_TASK_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_VARIABLE
    ADD CONSTRAINT ACT_FK_VAR_EXE FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_VARIABLE
    ADD CONSTRAINT ACT_FK_VAR_PROCINST FOREIGN KEY (PROC_INST_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_VARIABLE
    ADD CONSTRAINT ACT_FK_VAR_BYTEARRAY FOREIGN KEY (BYTEARRAY_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RU_JOB
    ADD CONSTRAINT ACT_FK_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_JOB
    ADD CONSTRAINT ACT_FK_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_JOB
    ADD CONSTRAINT ACT_FK_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_JOB
    ADD CONSTRAINT ACT_FK_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RU_TIMER_JOB
    ADD CONSTRAINT ACT_FK_TIMER_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_TIMER_JOB
    ADD CONSTRAINT ACT_FK_TIMER_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_TIMER_JOB
    ADD CONSTRAINT ACT_FK_TIMER_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_TIMER_JOB
    ADD CONSTRAINT ACT_FK_TIMER_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RU_SUSPENDED_JOB
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_SUSPENDED_JOB
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_SUSPENDED_JOB
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_SUSPENDED_JOB
    ADD CONSTRAINT ACT_FK_SUSPENDED_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RU_DEADLETTER_JOB
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_DEADLETTER_JOB
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_DEADLETTER_JOB
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_RU_DEADLETTER_JOB
    ADD CONSTRAINT ACT_FK_DEADLETTER_JOB_EXCEPTION FOREIGN KEY (EXCEPTION_STACK_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RU_EVENT_SUBSCR
    ADD CONSTRAINT ACT_FK_EVENT_EXEC FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RE_MODEL
    ADD CONSTRAINT ACT_FK_MODEL_SOURCE FOREIGN KEY (EDITOR_SOURCE_VALUE_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RE_MODEL
    ADD CONSTRAINT ACT_FK_MODEL_SOURCE_EXTRA FOREIGN KEY (EDITOR_SOURCE_EXTRA_VALUE_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_RE_MODEL
    ADD CONSTRAINT ACT_FK_MODEL_DEPLOYMENT FOREIGN KEY (DEPLOYMENT_ID_) REFERENCES ACT_RE_DEPLOYMENT (ID_);
ALTER TABLE ACT_PROCDEF_INFO
    ADD CONSTRAINT ACT_FK_INFO_JSON_BA FOREIGN KEY (INFO_JSON_ID_) REFERENCES ACT_GE_BYTEARRAY (ID_);
ALTER TABLE ACT_PROCDEF_INFO
    ADD CONSTRAINT ACT_FK_INFO_PROCDEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);
ALTER TABLE ACT_PROCDEF_INFO
    ADD CONSTRAINT ACT_UNIQ_INFO_PROCDEF UNIQUE (PROC_DEF_ID_);
ALTER TABLE ACT_RU_INTEGRATION
    ADD CONSTRAINT ACT_FK_INT_EXECUTION FOREIGN KEY (EXECUTION_ID_) REFERENCES ACT_RU_EXECUTION (ID_) ON DELETE CASCADE;
ALTER TABLE ACT_RU_INTEGRATION
    ADD CONSTRAINT ACT_FK_INT_PROC_INST FOREIGN KEY (PROCESS_INSTANCE_ID_) REFERENCES ACT_RU_EXECUTION (ID_);
ALTER TABLE ACT_RU_INTEGRATION
    ADD CONSTRAINT ACT_FK_INT_PROC_DEF FOREIGN KEY (PROC_DEF_ID_) REFERENCES ACT_RE_PROCDEF (ID_);