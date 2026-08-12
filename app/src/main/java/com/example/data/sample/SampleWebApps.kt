package com.example.data.sample

import android.content.Context
import com.example.data.database.WebAppEntity
import com.example.util.FileImporter
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object SampleWebApps {

    fun createDefaultSamples(context: Context): List<WebAppEntity> {
        val samples = mutableListOf<WebAppEntity>()

        // 1. Calculator
        val calcId = "sample_calculator"
        val calcDir = FileImporter.getAppDir(context, calcId)
        writeSampleFile(calcDir, "index.html", getCalculatorHtml())
        samples.add(
            WebAppEntity(
                id = calcId,
                title = "ماشین حساب پیشرفته",
                description = "ماشین حساب مدرن با طراحی گلاسمورفیسم، تاریخچه محاسبات و انیمیشن روان",
                iconType = "EMOJI",
                iconValue = "🧮",
                folderPath = calcId,
                entryPoint = "index.html",
                category = "ابزار",
                isFavorite = true,
                createdAt = System.currentTimeMillis() - 100000,
                lastOpenedAt = System.currentTimeMillis()
            )
        )

        // 2. Notes App
        val notesId = "sample_notes"
        val notesDir = FileImporter.getAppDir(context, notesId)
        writeSampleFile(notesDir, "index.html", getNotesHtml())
        samples.add(
            WebAppEntity(
                id = notesId,
                title = "دفترچه یادداشت دیجیتال",
                description = "برنامه مدیریت یادداشت‌ها با ذخیره‌سازی فوری در LocalStorage و جستجوی سریع",
                iconType = "EMOJI",
                iconValue = "📝",
                folderPath = notesId,
                entryPoint = "index.html",
                category = "کاربردی",
                isFavorite = true,
                createdAt = System.currentTimeMillis() - 90000,
                lastOpenedAt = System.currentTimeMillis() - 50000
            )
        )

        // 3. Snake Canvas Game
        val gameId = "sample_snake"
        val gameDir = FileImporter.getAppDir(context, gameId)
        writeSampleFile(gameDir, "index.html", getSnakeGameHtml())
        samples.add(
            WebAppEntity(
                id = gameId,
                title = "بازی ماری بوم (Snake)",
                description = "بازی کلاسیک ماری با بوم Canvas و کلیدهای لمسی جهت‌نما و ثبت رکورد",
                iconType = "EMOJI",
                iconValue = "🐍",
                folderPath = gameId,
                entryPoint = "index.html",
                category = "بازی",
                isFavorite = false,
                createdAt = System.currentTimeMillis() - 80000,
                lastOpenedAt = System.currentTimeMillis() - 40000
            )
        )

        // 4. Task Manager Kanban
        val taskId = "sample_tasks"
        val taskDir = FileImporter.getAppDir(context, taskId)
        writeSampleFile(taskDir, "index.html", getTaskManagerHtml())
        samples.add(
            WebAppEntity(
                id = taskId,
                title = "مدیریت کارهای روزانه",
                description = "بورد کانبان برای دسته‌بندی کارها (انجام، در حال انجام، تکمیل شده)",
                iconType = "EMOJI",
                iconValue = "📋",
                folderPath = taskId,
                entryPoint = "index.html",
                category = "مدیریت",
                isFavorite = true,
                createdAt = System.currentTimeMillis() - 70000,
                lastOpenedAt = System.currentTimeMillis() - 30000
            )
        )

        // 5. Pomodoro Clock
        val clockId = "sample_pomodoro"
        val clockDir = FileImporter.getAppDir(context, clockId)
        writeSampleFile(clockDir, "index.html", getPomodoroHtml())
        samples.add(
            WebAppEntity(
                id = clockId,
                title = "ساعت و تایمر پومودورو",
                description = "ساعت دیجیتال آنلاین همراه با تایمر تمرکز ۲۵ دقیقه‌ای و هشدارهای صوتی",
                iconType = "EMOJI",
                iconValue = "⏱️",
                folderPath = clockId,
                entryPoint = "index.html",
                category = "ابزار",
                isFavorite = false,
                createdAt = System.currentTimeMillis() - 60000,
                lastOpenedAt = System.currentTimeMillis() - 20000
            )
        )

        return samples
    }

    private fun writeSampleFile(dir: File, fileName: String, content: String) {
        val file = File(dir, fileName)
        FileOutputStream(file).use { out ->
            out.write(content.toByteArray(Charsets.UTF_8))
        }
    }

    private fun getCalculatorHtml(): String = """
        <!DOCTYPE html>
        <html lang="fa" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>ماشین حساب</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui, -apple-system, sans-serif; }
                body { background: linear-gradient(135deg, #0f172a, #1e293b); color: #f8fafc; min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }
                .calc { background: rgba(255,255,255,0.05); backdrop-filter: blur(16px); border: 1px solid rgba(255,255,255,0.1); padding: 24px; border-radius: 28px; width: 100%; max-width: 360px; box-shadow: 0 20px 40px rgba(0,0,0,0.5); }
                .screen { background: rgba(0,0,0,0.4); padding: 20px; border-radius: 18px; text-align: left; margin-bottom: 20px; word-wrap: break-word; }
                .history { font-size: 14px; color: #94a3b8; min-height: 20px; }
                .display { font-size: 36px; font-weight: bold; color: #38bdf8; min-height: 48px; }
                .buttons { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
                button { background: rgba(255,255,255,0.08); border: none; color: #f8fafc; font-size: 20px; font-weight: 600; padding: 18px; border-radius: 16px; cursor: pointer; transition: all 0.2s; }
                button:active { transform: scale(0.92); background: rgba(255,255,255,0.2); }
                button.op { background: #0284c7; color: white; }
                button.equal { background: #10b981; grid-column: span 2; }
                button.clear { background: #ef4444; }
            </style>
        </head>
        <body>
            <div class="calc">
                <div class="screen">
                    <div class="history" id="hist"></div>
                    <div class="display" id="disp">0</div>
                </div>
                <div class="buttons">
                    <button class="clear" onclick="clr()">C</button>
                    <button onclick="del()">⌫</button>
                    <button class="op" onclick="append('%')">%</button>
                    <button class="op" onclick="append('/')">÷</button>
                    
                    <button onclick="append('7')">7</button>
                    <button onclick="append('8')">8</button>
                    <button onclick="append('9')">9</button>
                    <button class="op" onclick="append('*')">×</button>
                    
                    <button onclick="append('4')">4</button>
                    <button onclick="append('5')">5</button>
                    <button onclick="append('6')">6</button>
                    <button class="op" onclick="append('-')">-</button>
                    
                    <button onclick="append('1')">1</button>
                    <button onclick="append('2')">2</button>
                    <button onclick="append('3')">3</button>
                    <button class="op" onclick="append('+')">+</button>
                    
                    <button onclick="append('0')">0</button>
                    <button onclick="append('.')">.</button>
                    <button class="equal" onclick="calc()">=</button>
                </div>
            </div>
            <script>
                let disp = document.getElementById('disp');
                let hist = document.getElementById('hist');
                let cur = '0';

                function update() { disp.innerText = cur; }
                function append(val) {
                    if (cur === '0' && val !== '.') cur = val;
                    else cur += val;
                    update();
                }
                function clr() { cur = '0'; hist.innerText = ''; update(); }
                function del() {
                    cur = cur.length > 1 ? cur.slice(0, -1) : '0';
                    update();
                }
                function calc() {
                    try {
                        let res = eval(cur.replace('×', '*').replace('÷', '/'));
                        hist.innerText = cur + ' =';
                        cur = String(res);
                        update();
                    } catch(e) {
                        cur = 'خطا';
                        update();
                        setTimeout(clr, 1500);
                    }
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    private fun getNotesHtml(): String = """
        <!DOCTYPE html>
        <html lang="fa" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>دفترچه یادداشت</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui, sans-serif; }
                body { background: #0f172a; color: #f1f5f9; padding: 20px; max-width: 600px; margin: 0 auto; }
                h1 { color: #38bdf8; margin-bottom: 20px; text-align: center; font-size: 24px; }
                .form { display: flex; flex-direction: column; gap: 12px; margin-bottom: 24px; background: #1e293b; padding: 16px; border-radius: 16px; }
                input, textarea { background: #0f172a; border: 1px solid #334155; color: white; padding: 12px; border-radius: 10px; outline: none; font-size: 15px; }
                button { background: #0284c7; color: white; border: none; padding: 12px; border-radius: 10px; font-weight: bold; cursor: pointer; }
                .note { background: #1e293b; padding: 16px; border-radius: 14px; margin-bottom: 12px; border-right: 4px solid #38bdf8; position: relative; }
                .note h3 { margin-bottom: 6px; font-size: 18px; color: #f8fafc; }
                .note p { font-size: 14px; color: #94a3b8; white-space: pre-wrap; }
                .del { position: absolute; left: 12px; top: 12px; background: #ef4444; color: white; border: none; padding: 6px 10px; border-radius: 8px; font-size: 12px; cursor: pointer; }
            </style>
        </head>
        <body>
            <h1>📝 یادداشت‌های من</h1>
            <div class="form">
                <input type="text" id="title" placeholder="عنوان یادداشت...">
                <textarea id="body" rows="3" placeholder="متن یادداشت..."></textarea>
                <button onclick="addNote()">ذخیره یادداشت</button>
            </div>
            <div id="notes"></div>
            <script>
                let notes = JSON.parse(localStorage.getItem('notes_app') || '[]');
                function render() {
                    let container = document.getElementById('notes');
                    container.innerHTML = '';
                    notes.forEach((n, idx) => {
                        let div = document.createElement('div');
                        div.className = 'note';
                        div.innerHTML = `
                            <h3>${'$'}{n.title}</h3>
                            <p>${'$'}{n.body}</p>
                            <button class="del" onclick="delNote(${'$'}{idx})">حذف</button>
                        `;
                        container.appendChild(div);
                    });
                }
                function addNote() {
                    let title = document.getElementById('title').value;
                    let body = document.getElementById('body').value;
                    if(!title && !body) return;
                    notes.unshift({ title, body, date: Date.now() });
                    localStorage.setItem('notes_app', JSON.stringify(notes));
                    document.getElementById('title').value = '';
                    document.getElementById('body').value = '';
                    render();
                }
                function delNote(idx) {
                    notes.splice(idx, 1);
                    localStorage.setItem('notes_app', JSON.stringify(notes));
                    render();
                }
                render();
            </script>
        </body>
        </html>
    """.trimIndent()

    private fun getSnakeGameHtml(): String = """
        <!DOCTYPE html>
        <html lang="fa" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <title>بازی Snake</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui; }
                body { background: #0b0f19; color: white; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; overflow: hidden; touch-action: none; }
                .header { margin-bottom: 12px; text-align: center; }
                canvas { background: #111827; border: 2px solid #374151; border-radius: 12px; }
                .controls { display: grid; grid-template-columns: repeat(3, 60px); gap: 10px; margin-top: 16px; }
                .btn { background: #1f2937; border: 1px solid #4b5563; color: white; height: 60px; border-radius: 12px; font-size: 24px; display: flex; align-items: center; justify-content: center; user-select: none; }
                .btn:active { background: #3b82f6; }
            </style>
        </head>
        <body>
            <div class="header">
                <h2>🐍 بازی ماری</h2>
                <div>امتیاز: <span id="score">0</span> | بالاترین: <span id="high">0</span></div>
            </div>
            <canvas id="game" width="300" height="300"></canvas>
            <div class="controls">
                <div></div>
                <div class="btn" onclick="setDir('UP')">▲</div>
                <div></div>
                <div class="btn" onclick="setDir('LEFT')">◀</div>
                <div class="btn" onclick="setDir('DOWN')">▼</div>
                <div class="btn" onclick="setDir('RIGHT')">▶</div>
            </div>
            <script>
                const canvas = document.getElementById('game');
                const ctx = canvas.getContext('2d');
                const grid = 15;
                let count = 0;
                let score = 0;
                let highScore = localStorage.getItem('snake_high') || 0;
                document.getElementById('high').innerText = highScore;

                let snake = { x: 150, y: 150, dx: grid, dy: 0, cells: [], maxCells: 4 };
                let apple = { x: 60, y: 60 };

                function getRandomInt(min, max) { return Math.floor(Math.random() * (max - min)) + min; }

                function setDir(d) {
                    if (d==='UP' && snake.dy===0) { snake.dx=0; snake.dy=-grid; }
                    if (d==='DOWN' && snake.dy===0) { snake.dx=0; snake.dy=grid; }
                    if (d==='LEFT' && snake.dx===0) { snake.dx=-grid; snake.dy=0; }
                    if (d==='RIGHT' && snake.dx===0) { snake.dx=grid; snake.dy=0; }
                }

                function loop() {
                    requestAnimationFrame(loop);
                    if (++count < 8) return;
                    count = 0;

                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    snake.x += snake.dx;
                    snake.y += snake.dy;

                    if (snake.x < 0) snake.x = canvas.width - grid;
                    else if (snake.x >= canvas.width) snake.x = 0;
                    if (snake.y < 0) snake.y = canvas.height - grid;
                    else if (snake.y >= canvas.height) snake.y = 0;

                    snake.cells.unshift({x: snake.x, y: snake.y});
                    if (snake.cells.length > snake.maxCells) snake.cells.pop();

                    ctx.fillStyle = '#ef4444';
                    ctx.fillRect(apple.x, apple.y, grid-1, grid-1);

                    ctx.fillStyle = '#10b981';
                    snake.cells.forEach((cell, index) => {
                        ctx.fillRect(cell.x, cell.y, grid-1, grid-1);
                        if (cell.x === apple.x && cell.y === apple.y) {
                            snake.maxCells++;
                            score += 10;
                            document.getElementById('score').innerText = score;
                            if(score > highScore) {
                                highScore = score;
                                localStorage.setItem('snake_high', highScore);
                                document.getElementById('high').innerText = highScore;
                            }
                            apple.x = getRandomInt(0, 20) * grid;
                            apple.y = getRandomInt(0, 20) * grid;
                        }
                        for (let i = index + 1; i < snake.cells.length; i++) {
                            if (cell.x === snake.cells[i].x && cell.y === snake.cells[i].y) {
                                snake.x = 150; snake.y = 150; snake.cells = []; snake.maxCells = 4;
                                snake.dx = grid; snake.dy = 0; score = 0;
                                document.getElementById('score').innerText = 0;
                            }
                        }
                    });
                }
                requestAnimationFrame(loop);
            </script>
        </body>
        </html>
    """.trimIndent()

    private fun getTaskManagerHtml(): String = """
        <!DOCTYPE html>
        <html lang="fa" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>مدیریت کارها</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui; }
                body { background: #0d1117; color: #c9d1d9; padding: 20px; }
                h1 { color: #58a6ff; margin-bottom: 20px; font-size: 22px; text-align: center; }
                .input-group { display: flex; gap: 10px; margin-bottom: 20px; }
                input { flex: 1; background: #161b22; border: 1px solid #30363d; color: white; padding: 12px; border-radius: 8px; }
                button.add { background: #238636; border: none; color: white; padding: 12px 20px; border-radius: 8px; font-weight: bold; }
                .list { display: flex; flex-direction: column; gap: 10px; }
                .item { background: #161b22; border: 1px solid #30363d; padding: 14px; border-radius: 10px; display: flex; align-items: center; justify-content: space-between; }
                .item.done { opacity: 0.5; text-decoration: line-through; }
                .actions { display: flex; gap: 8px; }
                .btn-sm { background: #21262d; border: 1px solid #30363d; color: #c9d1d9; padding: 6px 12px; border-radius: 6px; font-size: 13px; cursor: pointer; }
            </style>
        </head>
        <body>
            <h1>📋 مدیر کارهای روزانه</h1>
            <div class="input-group">
                <input type="text" id="taskInput" placeholder="عنوان کار جدید...">
                <button class="add" onclick="addTask()">افزودن</button>
            </div>
            <div class="list" id="taskList"></div>
            <script>
                let tasks = JSON.parse(localStorage.getItem('task_app') || '[]');
                function render() {
                    let list = document.getElementById('taskList');
                    list.innerHTML = '';
                    tasks.forEach((t, i) => {
                        let div = document.createElement('div');
                        div.className = 'item ' + (t.done ? 'done' : '');
                        div.innerHTML = `
                            <span>${'$'}{t.title}</span>
                            <div class="actions">
                                <button class="btn-sm" onclick="toggleTask(${'$'}{i})">${'$'}{t.done ? 'انجام نشده' : 'تکمیل'}</button>
                                <button class="btn-sm" style="color:#f85149;" onclick="delTask(${'$'}{i})">حذف</button>
                            </div>
                        `;
                        list.appendChild(div);
                    });
                }
                function addTask() {
                    let input = document.getElementById('taskInput');
                    if(!input.value.trim()) return;
                    tasks.push({ title: input.value.trim(), done: false });
                    localStorage.setItem('task_app', JSON.stringify(tasks));
                    input.value = '';
                    render();
                }
                function toggleTask(i) {
                    tasks[i].done = !tasks[i].done;
                    localStorage.setItem('task_app', JSON.stringify(tasks));
                    render();
                }
                function delTask(i) {
                    tasks.splice(i, 1);
                    localStorage.setItem('task_app', JSON.stringify(tasks));
                    render();
                }
                render();
            </script>
        </body>
        </html>
    """.trimIndent()

    private fun getPomodoroHtml(): String = """
        <!DOCTYPE html>
        <html lang="fa" dir="rtl">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>پومودورو</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: system-ui; }
                body { background: #18181b; color: white; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 100vh; text-align: center; padding: 20px; }
                .clock { font-size: 48px; font-weight: bold; color: #a1a1aa; margin-bottom: 30px; letter-spacing: 2px; }
                .timer-box { background: #27272a; padding: 30px; border-radius: 24px; border: 1px solid #3f3f46; width: 100%; max-width: 320px; }
                .time { font-size: 56px; font-weight: 800; color: #f43f5e; margin: 16px 0; }
                .controls { display: flex; gap: 12px; justify-content: center; }
                button { background: #f43f5e; border: none; color: white; padding: 12px 24px; border-radius: 12px; font-size: 16px; font-weight: bold; cursor: pointer; }
                button.sec { background: #3f3f46; }
            </style>
        </head>
        <body>
            <div class="clock" id="liveClock">00:00:00</div>
            <div class="timer-box">
                <h3>⏱️ تایمر پومودورو (تمرکز)</h3>
                <div class="time" id="timer">25:00</div>
                <div class="controls">
                    <button id="startBtn" onclick="toggleTimer()">شروع</button>
                    <button class="sec" onclick="resetTimer()">بازنشانی</button>
                </div>
            </div>
            <script>
                function updateClock() {
                    let d = new Date();
                    document.getElementById('liveClock').innerText = d.toLocaleTimeString('fa-IR');
                }
                setInterval(updateClock, 1000);
                updateClock();

                let timeLeft = 25 * 60;
                let timerId = null;

                function updateTimer() {
                    let m = Math.floor(timeLeft / 60).toString().padStart(2, '0');
                    let s = (timeLeft % 60).toString().padStart(2, '0');
                    document.getElementById('timer').innerText = `${'$'}{m}:${'$'}{s}`;
                }

                function toggleTimer() {
                    let btn = document.getElementById('startBtn');
                    if (timerId) {
                        clearInterval(timerId);
                        timerId = null;
                        btn.innerText = 'ادامه';
                    } else {
                        btn.innerText = 'توقف';
                        timerId = setInterval(() => {
                            if (timeLeft > 0) {
                                timeLeft--;
                                updateTimer();
                            } else {
                                clearInterval(timerId);
                                alert('زمان تمرکز به پایان رسید!');
                            }
                        }, 1000);
                    }
                }

                function resetTimer() {
                    if (timerId) clearInterval(timerId);
                    timerId = null;
                    timeLeft = 25 * 60;
                    document.getElementById('startBtn').innerText = 'شروع';
                    updateTimer();
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}
