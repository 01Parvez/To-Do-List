import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { Toaster } from 'react-hot-toast'

/**
 * Application entry point.
 * React 18 uses createRoot() instead of ReactDOM.render().
 */
ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <App />
        {/* Global toast notifications */}
        <Toaster
            position="top-right"
            toastOptions={{
                duration: 4000,
                style: {
                    background: '#1e293b',
                    color: '#f1f5f9',
                    border: '1px solid #334155',
                    borderRadius: '10px',
                    fontSize: '14px',
                },
                success: { iconTheme: { primary: '#22c55e', secondary: '#fff' } },
                error: { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
            }}
        />
    </React.StrictMode>
)
