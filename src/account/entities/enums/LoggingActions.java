package account.entities.enums;

import account.entities.EventLogger;

public enum LoggingActions {


        CREATE_USER {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        }, CHANGE_PASSWORD {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        },
        ACCESS_DENIED {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        }, LOGIN_FAILED {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        },

        GRANT_ROLE {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        }, REMOVE_ROLE {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        },

        LOCK_USER {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        }, UNLOCK_USER {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        },

        DELETE_USER {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        }, BRUTE_FORCE {
                @Override
                public EventLogger prepareLoggingForDatabase() {
                        return null;
                }
        };

        public abstract EventLogger prepareLoggingForDatabase();

}
