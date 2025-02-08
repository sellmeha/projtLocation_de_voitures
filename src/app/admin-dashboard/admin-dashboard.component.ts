import { Component, OnInit } from '@angular/core';
import { UserControllerService,AuthControllerService } from '../api';
import { Users } from '../api/model/users';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';


@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  standalone: true,
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  users: Users[] = [];

  constructor(private router: Router,private userService: UserControllerService,private authService: AuthControllerService) {}

  ngOnInit(): void {


    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAllUsers('response').subscribe({
      next: (response) => {

        const data = response.body;
        if (Array.isArray(data)) {
          this.users = data;
        } else {
          console.error('Réponse inattendue :', data);
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des utilisateurs:', err);
      }
    });
  }




  logout(): void {
    this.authService.logout().subscribe({
      next: () => {

        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Erreur lors de la déconnexion', err);
      }
    });
  }


}

