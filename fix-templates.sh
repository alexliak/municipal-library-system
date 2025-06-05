#!/bin/bash

# Fix all template files that have the header fragment issue
cd /home/alexa/development/municipal-library-system

# Fix loans templates
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Loans'\'')}/g' src/main/resources/templates/loans/list.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Checkout'\'')}/g' src/main/resources/templates/loans/checkout.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''My Loans'\'')}/g' src/main/resources/templates/loans/my-loans.html

# Fix users templates
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Users'\'')}/g' src/main/resources/templates/users/list.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Register'\'')}/g' src/main/resources/templates/users/register.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Profile'\'')}/g' src/main/resources/templates/users/profile.html

# Fix books templates
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Books'\'')}/g' src/main/resources/templates/books/list.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Book Details'\'')}/g' src/main/resources/templates/books/detail.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Book Form'\'')}/g' src/main/resources/templates/books/form.html

# Fix authors templates (already done for list.html but let's check others)
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Author Details'\'')}/g' src/main/resources/templates/authors/detail.html
sed -i 's/fragments\/header :: header}/fragments\/header :: header('\''Author Form'\'')}/g' src/main/resources/templates/authors/form.html

echo "Templates fixed!"
