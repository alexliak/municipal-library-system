#!/bin/bash

cd /home/alexa/development/municipal-library-system

# Add all changes
git add -A

# Commit
git commit -m "Fix final issues - profile and checkout functionality

- Fixed profile template syntax errors (fragments, dates, script paths)
- Added missing attributes to my-loans controller (statistics)
- Removed required checkbox from checkout form
- Added debugging logs to checkout process
- Disabled SSL for development (port 8080)
- Fixed HTTPS requirement in SecurityConfig
- Added LoanStatus import and stream collectors

All features now working:
- Member checkout fully functional
- All profiles (admin, librarian, member) accessible
- My loans page shows proper statistics"

# Push to GitHub
git push origin main

echo "Commit completed successfully!"
