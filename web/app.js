document.getElementById('go').addEventListener('click', async () => {
  const a = document.getElementById('a').value;
  const b = document.getElementById('b').value;
  const op = document.getElementById('op').value;
  const resEl = document.getElementById('result');
  resEl.textContent = 'Computing...';
  try {
    const r = await fetch(`/api/calc?op=${encodeURIComponent(op)}&a=${encodeURIComponent(a)}&b=${encodeURIComponent(b)}`);
    const json = await r.json();
    if (json.error) {
      resEl.textContent = 'Error: ' + json.error;
    } else {
      resEl.textContent = 'Result: ' + json.result;
    }
  } catch (e) {
    resEl.textContent = 'Request failed';
  }
});
