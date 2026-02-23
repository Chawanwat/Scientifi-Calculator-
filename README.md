# 🧮 Scientific Calculator (JavaFX)

เครื่องคิดเลขวิทยาศาสตร์ พัฒนาด้วย **Java + JavaFX**  
รองรับปุ่ม scientific, การคำนวณแบบ expression, และ UI โทน **MistyRose**

---

## 🚀 Features

### 🔢 Basic
- บวก ลบ คูณ หาร: `+  -  ×  ÷`
- เปอร์เซ็นต์: `%`
- วงเล็บ: `( )`
- ทศนิยม: `.`

### 🧪 Scientific
- ตรีโกณ: `sin`, `cos`, `tan` *(ค่าเป็นองศา / degrees)*
- ลอการิทึม: `ln`, `log`
- รากที่สอง: `√`
- ค่าสัมบูรณ์: `abs`
- ยกกำลัง: `x²`, `xʸ`
- กลับเศษส่วน: `1/x`
- แฟกทอเรียล: `n!`
- ค่าคงที่: `π`, `e`

### 🧰 Utility
- ลบตัวท้าย: `DEL`
- ล้างทั้งหมด: `Clear`
- เปลี่ยนเครื่องหมาย: `+/-`
- คำนวณผลลัพธ์: `Enter`

---

## 🧱 Tech Stack

- **Language:** Java
- **UI Framework:** JavaFX
- **Build Tool:** Gradle *(ถ้ามีในโปรเจกต์)*
- **Expression Engine:** Custom Parser (Recursive Descent)

---

## ⚙️ Installation & Run
- git clone https://github.com/Chawanwat/Scientifi-Calculator
- cd Scientifi-Calculator

---

## 📁 Project Structure

---
```text
project/
│
├── src/
│   ├── GUI.java
│   └── ScientificCalculator.java
│
├── gradle/
│   └── wrapper/
│
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
│
├── README.md
├── LICENSE
└── .gitignore
