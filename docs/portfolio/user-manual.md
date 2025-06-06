# User Manual - Municipal Library Management System

## Table of Contents
1. [Getting Started](#getting-started)
2. [System Requirements](#system-requirements)
3. [Login Instructions](#login-instructions)
4. [Member Guide](#member-guide)
5. [Librarian Guide](#librarian-guide)
6. [Administrator Guide](#administrator-guide)
7. [Common Tasks](#common-tasks)
8. [Troubleshooting](#troubleshooting)
9. [FAQ](#faq)

---

## 1. Getting Started

Welcome to the Municipal Library Management System! This system allows you to:
- Search and browse our book catalog
- Manage book loans and returns
- Track your reading history
- Access library services online 24/7

### First Time Users
If you're new to the library, you'll need to register for an account. Visit the library in person or contact us to get started.

### Accessing the System
1. Open your web browser (Chrome, Firefox, Safari, or Edge)
2. Navigate to: `https://library.municipality.gov` (or `https://localhost:8443` for development)
3. You'll see the login page

---

## 2. System Requirements

### Supported Browsers
- Google Chrome (version 90+)
- Mozilla Firefox (version 88+)
- Safari (version 14+)
- Microsoft Edge (version 90+)

### Device Requirements
- **Desktop/Laptop**: Any modern computer with internet access
- **Tablet**: iPad, Android tablets with updated browsers
- **Mobile**: Responsive design works on all smartphones

### Internet Connection
- Broadband connection recommended
- Mobile data (4G/5G) supported

---

## 3. Login Instructions

### Step 1: Navigate to Login Page
![Login Page]
When you access the system, you'll see the login page.

### Step 2: Enter Credentials
1. **Username**: Enter your library username
2. **Password**: Enter your password (case-sensitive)
3. **Remember Me**: Check this to stay logged in (on personal devices only)

### Step 3: Click Login
After entering your credentials, click the "Login" button.

### Common Login Issues
- **"Invalid credentials"**: Check username/password spelling and caps lock
- **"Account disabled"**: Contact library staff
- **Forgot password**: Click "Forgot Password" link or visit the library

### Security Tips
- Never share your login credentials
- Log out when using public computers
- Use a strong password (8+ characters, mixed case, numbers, symbols)

---

## 4. Member Guide

As a library member, you can access the following features:

### 4.1 Dashboard
After login, you'll see your personalized dashboard showing:
- Active loans with due dates
- Recommended books
- Library announcements
- Quick search bar

### 4.2 Searching for Books

#### Basic Search
1. Use the search bar at the top of any page
2. Enter book title, author name, or ISBN
3. Press Enter or click the search icon

#### Advanced Search
1. Click "Advanced Search" below the search bar
2. Filter by:
   - Genre (Fiction, Non-Fiction, Science, etc.)
   - Publication year
   - Availability status
   - Author nationality

#### Search Tips
- Use partial words: "Harr" finds "Harry Potter"
- Search is case-insensitive
- Use author's last name for better results

### 4.3 Viewing Book Details
Click on any book title to see:
- Full description and summary
- Author information
- Availability status
- User ratings and reviews
- Similar book recommendations

### 4.4 Managing Your Loans

#### View Active Loans
1. Click "My Loans" in the navigation menu
2. See all your current loans with:
   - Book titles
   - Checkout dates
   - Due dates (highlighted if overdue)
   - Days remaining

#### View Loan History
1. In "My Loans", click "History" tab
2. See all past loans with return dates
3. Filter by date range

#### Renewing Books
**Note**: Renewal must be done by library staff
1. Note the book you want to renew
2. Contact the library or visit in person
3. Renewals allowed if no reservations exist

### 4.5 Rating and Reviewing Books
After returning a book:
1. Go to the book's detail page
2. Click the star rating (1-5 stars)
3. Optionally add a written review
4. Click "Submit Rating"

### 4.6 Managing Your Profile

#### View Profile
1. Click your name in the top-right corner
2. Select "My Profile"

#### Update Information
You can update:
- Email address
- Phone number
- Mailing address
- Password (requires current password)

#### Change Password
1. Go to "My Profile"
2. Click "Change Password"
3. Enter current password
4. Enter new password twice
5. Click "Update Password"

---

## 5. Librarian Guide

Librarians have additional capabilities for managing library operations.

### 5.1 Librarian Dashboard
Your dashboard shows:
- Daily statistics (checkouts, returns, new members)
- Overdue books alert
- Low inventory warnings
- Recent activities

### 5.2 Book Management

#### Adding New Books
1. Navigate to "Books" → "Add New Book"
2. Fill in required fields:
   - ISBN (13 digits)
   - Title
   - Genre
   - Total copies
3. Add optional information:
   - Publication date
   - Summary
   - Cover image URL
4. Click "Add Authors" to associate authors
5. Click "Save Book"

#### Editing Book Information
1. Find the book using search
2. Click "Edit" button
3. Update necessary fields
4. Click "Save Changes"

#### Managing Inventory
1. Go to "Books" → "Inventory"
2. Update available copies when books are lost/damaged
3. View low stock alerts

### 5.3 Author Management

#### Adding Authors
1. Navigate to "Authors" → "Add Author"
2. Enter:
   - Full name
   - Biography (optional)
   - Birth date (optional)
   - Nationality (optional)
3. Click "Save Author"

#### Associating Authors with Books
1. When adding/editing a book
2. Click "Manage Authors"
3. Search and select authors
4. Click "Add Selected"

### 5.4 Loan Management

#### Processing Checkouts
1. Go to "Loans" → "New Checkout"
2. Select member (search by name/username)
3. Select book (search by title/ISBN)
4. Verify availability
5. Click "Checkout"
6. Due date automatically set to 14 days

#### Processing Returns
1. Go to "Loans" → "Active Loans"
2. Find the loan (search by member/book)
3. Click "Return" button
4. Confirm the return

#### Managing Overdue Books
1. Go to "Loans" → "Overdue"
2. See all overdue loans
3. Contact members as needed
4. Process returns when books arrive

### 5.5 Member Services

#### Viewing Member Information
1. Go to "Members" → "View All"
2. Search for specific member
3. Click on member name to see:
   - Contact information
   - Current loans
   - Loan history

#### Helping Members
- Reset passwords (requires admin)
- Update contact information
- Explain policies
- Process special requests

---

## 6. Administrator Guide

Administrators have full system access including user management and reporting.

### 6.1 User Management

#### Creating New Users
1. Navigate to "Users" → "Add User"
2. Enter required information:
   - Username (unique)
   - Email (unique)
   - Full name
   - Temporary password
3. Select role:
   - Member (default)
   - Librarian
   - Administrator
4. Click "Create User"

#### Managing Existing Users
1. Go to "Users" → "Manage Users"
2. Search for user
3. Options:
   - Edit profile information
   - Change roles
   - Enable/disable account
   - Reset password

#### Role Assignment
1. Find user in user list
2. Click "Edit Roles"
3. Check/uncheck role boxes
4. Click "Save Roles"

### 6.2 System Reports

#### Generating Reports
1. Navigate to "Reports"
2. Select report type:
   - Inventory Report
   - Loan Statistics
   - User Activity
   - Overdue Analysis
3. Set date range (if applicable)
4. Choose format (View/PDF)
5. Click "Generate"

#### Available Reports
- **Inventory Report**: Current stock levels, low inventory alerts
- **Circulation Report**: Checkout/return statistics
- **Popular Books**: Most borrowed titles
- **Member Activity**: Active vs inactive members
- **Financial Summary**: Late fees, lost books

#### Exporting Data
1. Generate any report
2. Click "Export to PDF"
3. Save or print as needed

### 6.3 System Configuration

#### Library Settings
1. Go to "Settings" → "Library Configuration"
2. Configurable options:
   - Loan period (default 14 days)
   - Maximum renewals
   - Low stock threshold
   - Late fee amounts

#### Security Settings
- Session timeout duration
- Password requirements
- Maximum login attempts
- Account lockout duration

### 6.4 Monitoring and Maintenance

#### System Health
1. Dashboard shows:
   - Active users
   - System performance
   - Database status
   - Recent errors

#### Backup Procedures
- Automatic daily backups
- Manual backup option
- Restore procedures (contact IT)

---

## 7. Common Tasks

### 7.1 For All Users

#### Logging Out
1. Click your username (top-right)
2. Select "Logout"
3. Confirm logout

#### Searching for a Book
1. Use search bar on any page
2. Enter title or author
3. View results
4. Click book for details

### 7.2 For Members

#### Checking Due Dates
1. Go to "My Loans"
2. View due dates in red if overdue
3. Plan library visit accordingly

#### Finding New Books
1. Browse by genre
2. Check "New Arrivals" section
3. View staff recommendations

### 7.3 For Staff

#### Daily Opening Tasks
1. Log in to system
2. Check overdue report
3. Process returned books
4. Review low inventory alerts

#### End of Day Tasks
1. Process all returns
2. Run daily report
3. Check tomorrow's holds
4. Log out of system

---

## 8. Troubleshooting

### 8.1 Login Problems

**Cannot Login**
- Verify username spelling
- Check CAPS LOCK
- Try password reset
- Contact library if locked out

**Session Expired**
- For security, sessions expire after 30 minutes
- Simply log in again
- Check "Remember Me" for longer sessions

### 8.2 Search Issues

**No Results Found**
- Check spelling
- Try fewer words
- Use partial terms
- Remove special characters

**Too Many Results**
- Add more search terms
- Use advanced filters
- Specify genre or year

### 8.3 Display Problems

**Page Not Loading**
- Check internet connection
- Clear browser cache
- Try different browser
- Disable ad blockers

**Buttons Not Working**
- Enable JavaScript
- Update browser
- Disable browser extensions
- Try incognito/private mode

### 8.4 Error Messages

**"Book Not Available"**
- All copies are checked out
- Try reserving the book
- Check similar titles

**"Access Denied"**
- You don't have permission
- Contact library staff
- Verify you're logged in

**"Invalid Request"**
- Refresh the page
- Log out and back in
- Clear browser data

---

## 9. Frequently Asked Questions

### General Questions

**Q: How do I get a library account?**
A: Visit the library with photo ID and proof of address. Staff will create your account.

**Q: Is the online system free?**
A: Yes, for all library members.

**Q: Can I access from home?**
A: Yes, from any internet-connected device.

### Account Questions

**Q: I forgot my password. What do I do?**
A: Click "Forgot Password" on login page or visit the library for assistance.

**Q: Can I change my username?**
A: No, usernames are permanent. Contact library for special circumstances.

**Q: How do I update my email?**
A: Go to "My Profile" and click "Edit Profile".

### Book Questions

**Q: How many books can I borrow?**
A: Members can borrow up to 5 books at a time.

**Q: How long can I keep books?**
A: Standard loan period is 14 days.

**Q: Can I renew books online?**
A: Not currently. Visit or call the library to renew.

**Q: What happens if I return books late?**
A: Late fees may apply. Check with library for current policy.

### Technical Questions

**Q: Which browser should I use?**
A: Any modern browser works. Chrome and Firefox are recommended.

**Q: Is my information secure?**
A: Yes, we use encryption and follow security best practices.

**Q: Can I use my phone?**
A: Yes, the system is mobile-friendly.

**Q: The site looks different on my phone.**
A: The responsive design adapts to your screen size.

---

## Contact Information

### Need Help?
- **Email**: support@municipallibrary.gov
- **Phone**: (555) 123-4567
- **In Person**: Main Library Help Desk

### Hours
- **System**: Available 24/7
- **Support**: Monday-Friday 9 AM - 5 PM
- **Library**: Monday-Saturday 9 AM - 8 PM

### Feedback
I am welcoming your suggestions! Use the "Feedback" link at the bottom of any page.

---

*User Manual Version 1.0*  
*Last Updated: June 6, 2025*  
*© Municipal Library System*