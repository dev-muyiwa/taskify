-- liquibase formatted sql

-- changeset DELL:1756850311059-1
ALTER TABLE workspace_member_invites ADD CONSTRAINT uc_21d570ca8daae111f2c48a91a UNIQUE (workspace_id);

