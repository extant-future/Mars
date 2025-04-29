# Gray Configuration Usage Tips

To enable gray configuration in your environment, follow these steps:

1. **Set Environment Variable**  
   ```bash
   export USE_GRAY_CONFIG=true
   ```

2. **Update Configuration File**  
   Add the following to your config file (e.g., `settings.py` or `config.json`):
   ```python
   # settings.py
   GRAY_MODE = True
   ```

3. **Restart Services**  
   Restart any services that depend on configuration changes:
   ```bash
   sudo systemctl restart mars-service
   ```

4. **Verify Configuration**  
   Check the active configuration with:
   ```bash
   mars-cli config status
   ```

*Note: Gray mode reduces resource allocation by 30% for non-critical operations.*