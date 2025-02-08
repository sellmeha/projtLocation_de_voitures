import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';


import { ApiModule } from './api/api.module';


import { LoginComponent } from './login/login.component';
import {AuthControllerService, AuthInterceptor} from './api/api/authController.service';
import { RegisterComponent } from './register/register.component';
import {CommonModule} from '@angular/common';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import {UserControllerService} from './api';

import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';

@NgModule({
  declarations: [
    AppComponent,


  ],
  imports: [
    UserDashboardComponent,
    AdminDashboardComponent,
    RegisterComponent,
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ApiModule,
    LoginComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    AuthControllerService,
    UserControllerService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
