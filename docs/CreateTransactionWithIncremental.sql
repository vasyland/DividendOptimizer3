-- Create a new transaction with incremental services: incrementalHoldingsService, incrementalPortfolioSummaryService

-- clean tables
use golem;
TRUNCATE TABLE golem.holding;
TRUNCATE TABLE golem.portfolio_summary;
TRUNCATE TABLE golem.transaction;
