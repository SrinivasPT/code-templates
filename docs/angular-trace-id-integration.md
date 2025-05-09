# Angular Trace ID Integration

This document describes how to integrate Angular frontend applications with the backend's distributed tracing system.

## Overview

The backend API supports receiving a trace ID from client applications, which enables end-to-end tracing across the full stack. When a trace ID is provided by the client, the backend will use it throughout its processing and include it in all logs.

## Implementation in Angular

### 1. HTTP Interceptor

Create an HTTP interceptor to add the trace ID to all outgoing requests:

```typescript
// trace-id.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TraceService } from './trace.service';

@Injectable()
export class TraceIdInterceptor implements HttpInterceptor {
  constructor(private traceService: TraceService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Get current trace ID or generate a new one
    const traceId = this.traceService.getCurrentTraceId();

    // Clone the request and add the trace ID header
    const modifiedRequest = request.clone({
      setHeaders: {
        'X-Trace-ID': traceId
      }
    });

    return next.handle(modifiedRequest);
  }
}
```

### 2. Trace Service

Create a service to manage trace IDs:

```typescript
// trace.service.ts
import { Injectable } from '@angular/core';
import { v4 as uuidv4 } from 'uuid'; // Requires npm install uuid + @types/uuid

@Injectable({
  providedIn: 'root'
})
export class TraceService {
  private currentTraceId: string | null = null;

  constructor() {}

  /**
   * Returns the current trace ID or generates a new one
   */
  getCurrentTraceId(): string {
    if (!this.currentTraceId) {
      this.currentTraceId = uuidv4();
    }
    return this.currentTraceId;
  }

  /**
   * Resets the trace ID (useful when starting a new user flow)
   */
  resetTraceId(): string {
    this.currentTraceId = uuidv4();
    return this.currentTraceId;
  }

  /**
   * Set a specific trace ID (useful for testing or when receiving from elsewhere)
   */
  setTraceId(traceId: string): void {
    this.currentTraceId = traceId;
  }
}
```

### 3. Register the Interceptor

Register the interceptor in your app module:

```typescript
// app.module.ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TraceIdInterceptor } from './trace-id.interceptor';

@NgModule({
  // ...
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TraceIdInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

## Trace ID Lifecycle

- By default, a new trace ID is generated when the application starts
- A trace ID persists throughout a user's session or flow
- You can call `traceService.resetTraceId()` to start a new trace for a new flow
- The trace ID is automatically added to all HTTP requests to the backend

## Logging in Angular

For comprehensive tracing, you might also want to log the trace ID in your Angular application:

```typescript
// logger.service.ts
import { Injectable } from '@angular/core';
import { TraceService } from './trace.service';

@Injectable({
  providedIn: 'root'
})
export class LoggerService {
  constructor(private traceService: TraceService) {}

  info(message: string, data?: any): void {
    console.info(
      `[${new Date().toISOString()}] [TraceID: ${this.traceService.getCurrentTraceId()}] ${message}`, 
      data || ''
    );
  }

  error(message: string, error?: any): void {
    console.error(
      `[${new Date().toISOString()}] [TraceID: ${this.traceService.getCurrentTraceId()}] ${message}`, 
      error || ''
    );
  }

  // Add additional logging methods as needed
}
```

## Backend Header Name

The backend expects the trace ID in a header named `X-Trace-ID`.
