// eslint.config.js
import js from '@eslint/js';
import tseslint from 'typescript-eslint';

export default tseslint.config(
    js.configs.recommended,
    ...tseslint.configs.recommended,
    {
        ignores: [
            "dist/**",
            ".angular/**",
            "node_modules/**",
            "vite.config.ts"
        ]
    },
    {
        files: ["src/**/*.ts", "*.js"],
        languageOptions: {
            globals: {
                window: "readonly",
                document: "readonly",
                console: "readonly",
                setTimeout: "readonly",
                clearTimeout: "readonly",
                localStorage: "readonly",
                sessionStorage: "readonly",
                File: "readonly",
                Blob: "readonly",
                FormData: "readonly",
                navigator: "readonly",
                URL: "readonly",
                module: "readonly",
                require: "readonly",
                process: "readonly",
                __dirname: "readonly"
            }
        },
        rules: {
            "@typescript-eslint/no-explicit-any": "off",
            "@typescript-eslint/no-unused-vars": ["warn", { "argsIgnorePattern": "^_" }],
            "@typescript-eslint/no-unused-expressions": "off",
            "no-console": "off",
            "no-undef": "off"
        }
    }
);