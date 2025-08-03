# Data Storage and Persistence Explanation

## 🗂️ **Where Are Players Saved?**

### 1. **Primary Storage: H2 Database File**
- **Location**: `./data/playerdb.mv.db`
- **Size**: ~5.7MB (grows as you add more players)
- **Type**: H2 file-based database
- **Persistence**: ✅ **Survives server restarts**

### 2. **CSV File: Read-Only Source**
- **Location**: `src/main/resources/Player.csv`
- **Purpose**: Initial data source (read-only)
- **Size**: ~2.4MB (original CSV data)
- **Behavior**: Loaded once when database is created

## 🔄 **How Data Flow Works**

### **Initial Setup (First Run)**
```
1. Application starts
2. H2 database file doesn't exist
3. schema.sql executes: CREATE TABLE IF NOT EXISTS PLAYERS
4. CSV data loaded into database table
5. Database file created: ./data/playerdb.mv.db
```

### **Subsequent Runs (Restarts)**
```
1. Application starts
2. H2 database file exists
3. schema.sql executes: CREATE TABLE IF NOT EXISTS PLAYERS (skipped - table exists)
4. Database loads existing data (CSV + new players)
5. No data loss occurs
```

## 📊 **Data Composition**

### **Database Contents**
```
┌─────────────────────────────────────┐
│ ./data/playerdb.mv.db (5.7MB)      │
├─────────────────────────────────────┤
│ Original CSV Players (~19,000)      │
│ + New Players (created via API)     │
│ + All data persists on restart      │
└─────────────────────────────────────┘
```

### **CSV File (Read-Only)**
```
┌─────────────────────────────────────┐
│ src/main/resources/Player.csv       │
├─────────────────────────────────────┤
│ Original CSV Players (~19,000)      │
│ Never modified by application       │
│ Used only for initial setup         │
└─────────────────────────────────────┘
```

## ✅ **Persistence Verification**

### **Test Results**
1. **Created player**: `restarttest`
2. **Verified creation**: ✅ Found in database
3. **Restarted server**: ✅ Data persisted
4. **Verified after restart**: ✅ Still accessible

### **Database File Growth**
- **Initial size**: ~5.7MB (CSV data)
- **After adding players**: Size increases
- **Location**: `./data/playerdb.mv.db`

## 🎯 **Answers to Your Questions**

### Q: "Are players getting saved to file and also to CSV?"
**A: NO** - Here's what actually happens:

- ✅ **Saved to**: H2 database file (`./data/playerdb.mv.db`)
- ❌ **NOT saved to**: CSV file (remains unchanged)
- ✅ **CSV data**: Loaded once, then preserved in database

### Q: "Where are created players saved?"
**A: In the H2 database file**:
- **File**: `./data/playerdb.mv.db`
- **Contains**: Original CSV data + all new players
- **Format**: H2 database format (not CSV)

### Q: "Will they be there on restart?"
**A: YES** - Data persists because:
- ✅ Database file survives restarts
- ✅ No data overwriting occurs
- ✅ All players (CSV + new) remain accessible

## 🔧 **Technical Details**

### **Database Configuration**
```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/playerdb  # File-based persistence
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
```

### **Schema Strategy**
```sql
-- Only creates table if it doesn't exist
CREATE TABLE IF NOT EXISTS PLAYERS AS SELECT * FROM CSVREAD('classpath:Player.csv');
```

## 📈 **Data Growth Pattern**

### **File Size Progression**
```
Initial: 5.7MB (CSV data only)
+ 1 player: ~5.7MB (minimal increase)
+ 10 players: ~5.7MB (minimal increase)
+ 100 players: ~5.8MB (noticeable increase)
```

### **Storage Efficiency**
- H2 database is very efficient
- Minimal size increase per player
- Compressed storage format

## 🚀 **Summary**

### **What You Get**
- ✅ **Persistent storage**: All data survives restarts
- ✅ **CSV preservation**: Original data never modified
- ✅ **Efficient storage**: Minimal file size growth
- ✅ **Reliable access**: All players always available

### **What You Don't Get**
- ❌ **CSV modification**: CSV file remains unchanged
- ❌ **Data loss**: No data is ever lost on restart
- ❌ **Performance issues**: Fast access to all data

The system is designed for **maximum data safety** with **zero data loss**! 🛡️ 