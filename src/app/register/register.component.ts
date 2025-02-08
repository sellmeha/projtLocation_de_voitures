import { Component } from '@angular/core';
import { AuthControllerService } from '../api/api/authController.service';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {AuthRequestDTO} from '../api';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  imports: [
    FormsModule,
    CommonModule
  ],
  standalone: true,
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  isLoading: boolean = false;
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';


  constructor(private authService: AuthControllerService,private router: Router) {}
  register() {

    this.successMessage = '';
    this.errorMessage = '';


    if (!this.username || !this.password) {
      this.errorMessage = 'Username and password are required.';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    const user: AuthRequestDTO = { username: this.username, password: this.password };

    this.isLoading = true;
    this.authService.registerUser(user).subscribe({
      next: (response: string) => {
        this.successMessage = response;
        this.router.navigate(['/login']);
        this.errorMessage = '';
      },
      error: (error) => {
        console.error('Registration error:', error);


        if (error.status === 400) {
          this.errorMessage = error.error || 'Bad request. Please check your input.';
        } else if (error.status === 409) {
          this.errorMessage = 'Username already exists. Please choose a different one.';
        } else {
          this.errorMessage = 'An unexpected error occurred. Please try again.';
        }
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }


}
