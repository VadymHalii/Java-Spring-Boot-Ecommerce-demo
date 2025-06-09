# Product Management Application

## Database Options

### Option 1: H2 Database (Recommended for Testing)
```bash
# Run with H2 (no PostgreSQL needed)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Access application: http://localhost:8080
# Access H2 console: http://localhost:8080/h2-console
# H2 URL: jdbc:h2:file:./data/demo
# Username: sa
# Password: (leave empty)
```

### Option 2: PostgreSQL with Docker (Production-like)
```bash
# Start PostgreSQL
sudo docker-compose up -d

# Run application
./gradlew bootRun

# Stop PostgreSQL
sudo docker-compose down
```

### Option 3: Install PostgreSQL Locally
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql
CREATE DATABASE demo;
CREATE USER demo WITH PASSWORD 'demo';
GRANT ALL PRIVILEGES ON DATABASE demo TO demo;
\q

# Run application
./gradlew bootRun
```

## Quick Start (H2 Database)

1. **Start the application:**
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=dev'
   ```

2. **Open browser:** http://localhost:8080

3. **Test the features:**
   - Click "Load Products from API" → Fetches products
   - Use search box for real-time filtering
   - Add new products using the form
   - Edit/Delete existing products

## Features Implemented

✅ **Core Requirements:**
- Spring Boot + Kotlin
- H2/PostgreSQL database support
- Flyway migrations (PostgreSQL) / JPA DDL (H2)
- HTMX for dynamic UI
- Shoelace components
- Scheduled API fetching

✅ **Functionality:**
- Load products from external API
- Display products in table
- Add new products without page reload
- Real-time search
- Category filtering
- Edit products
- Delete with confirmation
- JSONB/TEXT variants storage

✅ **Additional Features:**
- Hourly scheduled sync
- Clean responsive UI
- Error handling
- Test configuration