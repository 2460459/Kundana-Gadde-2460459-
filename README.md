# Basic Java Calculator (Web UI & Codespaces)

This project includes a simple web UI and a lightweight HTTP server so you can run the calculator in a GitHub Codespace and open it in your browser.

How to use in Codespaces

1. Open this repository in a GitHub Codespace.
2. The devcontainer will build and start the server automatically and forward port 8080.
3. Codespaces should offer to open the forwarded port in the browser; the UI will be available at that forwarded URL.

Run locally

From repository root:

```bash
chmod +x run.sh
./run.sh
```

Then open http://localhost:8080 in your browser.

Notes:
- CalculatorServer and Calculator must be in the same (default) package. The earlier Calculator.java in the repository uses no package declaration, so CalculatorServer is provided without any `package` line.
- If you prefer a named package (recommended for larger projects), tell me the desired package name (e.g. `com.example.calc`) and I will update all Java files and the compile/run steps accordingly.
