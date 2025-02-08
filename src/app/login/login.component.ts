import { Component } from '@angular/core';
import { AuthControllerService } from '../api/api/authController.service';
import {FormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  errorMessage: string = '';

  username: string = '';
  password: string = '';


  constructor(private authController: AuthControllerService , private router: Router) {}


  login() {
    const credentials = {
      username: this.username,
      password: this.password
    };


    this.authController.authenticateUser(credentials).subscribe({
      next: () => {
        console.log('Authentification réussie');

        this.authController.getUserRole().subscribe({
          next: role => {
            const dashboard = role === 'ADMIN' ? '/admin-Dash' : 'user-dash';
            this.router.navigate([dashboard]);
          },
          error: err => {
            console.error('Erreur lors de la récupération du rôle', err);
            this.errorMessage = 'Impossible de récupérer le rôle de l\'utilisateur.';
          }
        });
      },
      error: err => {
        console.error('Erreur d\'authentification', err);
        this.errorMessage = 'Nom d\'utilisateur ou mot de passe incorrect.';
      }
    });
  }


}
