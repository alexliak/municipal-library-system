#!/bin/bash
# Script to check which HTML files are using the wrong template

echo "Checking HTML files for Bootstrap 5 (wrong template)..."
echo "================================================"

cd /home/alexa/development/municipal-library-system/src/main/resources/templates

# Find all HTML files that contain Bootstrap 5 CDN
files_with_wrong_template=$(grep -r "bootstrap@5" . --include="*.html" | grep -v node_modules | cut -d: -f1 | sort | uniq)

if [ -z "$files_with_wrong_template" ]; then
    echo "✅ All files are using the correct template!"
else
    echo "❌ Files using wrong template (Bootstrap 5):"
    echo "$files_with_wrong_template"
fi

echo ""
echo "Checking HTML files WITHOUT SB Admin 2 fragments..."
echo "================================================"

# Find all HTML files that don't have the proper fragment includes
files_without_fragments=$(find . -name "*.html" -type f ! -path "./fragments/*" -exec grep -L "fragments/sidebar" {} \; | sort)

echo "Files without sidebar fragment:"
echo "$files_without_fragments"