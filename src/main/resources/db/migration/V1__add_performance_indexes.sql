-- Add performance indexes for better query performance

-- Index on email field for faster user lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users (LOWER(email));

-- Index on created_at for time-based queries
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users (created_at);

-- Index on workspace_id for workspace member queries
CREATE INDEX IF NOT EXISTS idx_workspace_members_workspace_id ON workspace_members (workspace_id);

-- Index on user_id for workspace member queries
CREATE INDEX IF NOT EXISTS idx_workspace_members_user_id ON workspace_members (user_id);

-- Composite index for workspace member lookups
CREATE INDEX IF NOT EXISTS idx_workspace_members_workspace_user ON workspace_members (workspace_id, user_id);

-- Index on workspace name for search functionality
CREATE INDEX IF NOT EXISTS idx_workspaces_name ON workspaces (name);

-- Index on created_at for workspaces
CREATE INDEX IF NOT EXISTS idx_workspaces_created_at ON workspaces (created_at); 