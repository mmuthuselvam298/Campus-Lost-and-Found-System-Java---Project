import React, { useState } from 'react';
import {
  Sparkles,
  Search,
  PlusCircle,
  HelpCircle,
  CheckCircle2,
  Bell,
  Shield,
  QrCode,
  Layers,
  UserCheck,
  ChevronDown
} from 'lucide-react';
import { User, NotificationItem } from '../types';

interface NavbarProps {
  currentView: string;
  onNavigate: (view: string) => void;
  currentUser: User | null;
  onSwitchUser: (email: string) => void;
  onOpenAuth?: () => void;
  notifications: NotificationItem[];
  unreadCount: number;
  onMarkRead: (id: number) => void;
  matchCount: number;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentView,
  onNavigate,
  currentUser,
  onSwitchUser,
  onOpenAuth,
  notifications,
  unreadCount,
  onMarkRead,
  matchCount
}) => {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  return (
    <header className="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-slate-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div
            className="flex items-center space-x-3 cursor-pointer select-none"
            onClick={() => onNavigate('home')}
          >
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 via-indigo-600 to-cyan-400 flex items-center justify-center text-white shadow-md shadow-brand-500/20">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="font-bold text-xl tracking-tight text-slate-900">CampusFind</span>
                <span className="bg-gradient-to-r from-brand-600 to-indigo-600 text-transparent bg-clip-text font-black text-lg">AI</span>
              </div>
              <p className="text-[10px] text-slate-500 font-medium tracking-wide uppercase">Smart Campus Lost & Found</p>
            </div>
          </div>

          {/* Desktop Navigation Links */}
          <nav className="hidden md:flex items-center space-x-1">
            <button
              onClick={() => onNavigate('browse')}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'browse'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <Search className="w-4 h-4" />
              <span>Browse Items</span>
            </button>

            <button
              onClick={() => onNavigate('report-found')}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'report-found'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <PlusCircle className="w-4 h-4 text-emerald-600" />
              <span>Report Found</span>
              <span className="bg-emerald-100 text-emerald-700 text-[10px] font-bold px-1.5 py-0.5 rounded-full uppercase">AI Vision</span>
            </button>

            <button
              onClick={() => onNavigate('report-lost')}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'report-lost'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <HelpCircle className="w-4 h-4 text-amber-600" />
              <span>Report Lost</span>
            </button>

            <button
              onClick={() => onNavigate('matches')}
              className={`relative px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'matches'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <Sparkles className="w-4 h-4 text-indigo-600" />
              <span>Match Center</span>
              {matchCount > 0 && (
                <span className="bg-indigo-600 text-white text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center animate-pulse">
                  {matchCount}
                </span>
              )}
            </button>

            <button
              onClick={() => onNavigate('my-activity')}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'my-activity'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <Layers className="w-4 h-4" />
              <span>My Activity</span>
            </button>

            <button
              onClick={() => onNavigate('pickup')}
              className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                currentView === 'pickup'
                  ? 'bg-brand-50 text-brand-700 font-semibold'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
              }`}
            >
              <QrCode className="w-4 h-4 text-purple-600" />
              <span>Pickup & QR</span>
            </button>

            {currentUser?.role === 'ROLE_ADMIN' && (
              <button
                onClick={() => onNavigate('admin')}
                className={`px-3 py-2 rounded-lg text-sm font-medium transition-all flex items-center space-x-1.5 ${
                  currentView === 'admin'
                    ? 'bg-purple-50 text-purple-700 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Shield className="w-4 h-4 text-purple-600" />
                <span>Admin Panel</span>
              </button>
            )}
          </nav>

          {/* Right Action Icons & Role Switcher */}
          <div className="flex items-center space-x-3">
            {/* Notification Bell */}
            <div className="relative">
              <button
                onClick={() => setShowNotifications(!showNotifications)}
                className="relative p-2 rounded-xl text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors focus:outline-none"
                title="Notifications"
              >
                <Bell className="w-5 h-5" />
                {unreadCount > 0 && (
                  <span className="absolute top-1.5 right-1.5 w-2.5 h-2.5 bg-red-500 rounded-full ring-2 ring-white"></span>
                )}
              </button>

              {/* Notification Dropdown */}
              {showNotifications && (
                <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-white rounded-2xl shadow-xl border border-slate-200 overflow-hidden z-50 animate-in fade-in slide-in-from-top-2 duration-200">
                  <div className="p-3.5 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
                    <span className="font-semibold text-sm text-slate-800">Campus Notifications</span>
                    <span className="text-xs bg-brand-100 text-brand-700 font-bold px-2 py-0.5 rounded-full">
                      {unreadCount} Unread
                    </span>
                  </div>
                  <div className="max-h-80 overflow-y-auto divide-y divide-slate-100">
                    {notifications.length === 0 ? (
                      <div className="p-6 text-center text-sm text-slate-500">
                        No notifications yet. You will be alerted when a match or claim is made.
                      </div>
                    ) : (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          onClick={() => {
                            onMarkRead(n.id);
                            if (n.linkUrl) onNavigate(n.linkUrl.replace('/', ''));
                            setShowNotifications(false);
                          }}
                          className={`p-3.5 hover:bg-slate-50 cursor-pointer transition-colors ${
                            !n.isRead ? 'bg-brand-50/40' : ''
                          }`}
                        >
                          <div className="flex items-start space-x-2">
                            <span className="w-2 h-2 mt-1.5 rounded-full bg-brand-500 flex-shrink-0" />
                            <div>
                              <p className="text-xs font-bold text-slate-900">{n.title}</p>
                              <p className="text-xs text-slate-600 mt-0.5">{n.message}</p>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* Role Switcher Pill */}
            <div className="relative">
              <button
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center space-x-2 py-1.5 px-3 bg-slate-100 hover:bg-slate-200 rounded-xl border border-slate-200 text-xs transition-colors"
              >
                <div className="w-6 h-6 rounded-lg bg-brand-600 text-white flex items-center justify-center font-bold text-[10px]">
                  {currentUser ? currentUser.fullName.charAt(0) : 'U'}
                </div>
                <div className="text-left hidden sm:block">
                  <p className="font-bold text-slate-800 leading-tight">{currentUser?.fullName || 'Alex Chen'}</p>
                  <p className="text-[10px] text-slate-500 capitalize">
                    {currentUser?.role.replace('ROLE_', '').toLowerCase() || 'Student'}
                  </p>
                </div>
                <ChevronDown className="w-3.5 h-3.5 text-slate-500" />
              </button>

              {/* Role Switcher Dropdown */}
              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-64 bg-white rounded-2xl shadow-xl border border-slate-200 p-2 z-50">
                  <div className="px-3 py-2 border-b border-slate-100 mb-1 flex items-center justify-between">
                    <div>
                      <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Quick Demo Switcher</p>
                      <p className="text-[11px] text-slate-600">Simulate different accounts:</p>
                    </div>
                    {onOpenAuth && (
                      <button
                        onClick={() => {
                          setShowUserMenu(false);
                          onOpenAuth();
                        }}
                        className="text-[11px] font-bold text-brand-600 hover:text-brand-700 bg-brand-50 px-2 py-1 rounded-lg"
                      >
                        Sign In / Reg
                      </button>
                    )}
                  </div>

                  <button
                    onClick={() => {
                      onSwitchUser('student@campus.edu');
                      setShowUserMenu(false);
                    }}
                    className={`w-full text-left p-2 rounded-xl text-xs flex items-center space-x-2.5 transition-colors ${
                      currentUser?.email === 'student@campus.edu' ? 'bg-brand-50 text-brand-700 font-bold' : 'hover:bg-slate-50 text-slate-700'
                    }`}
                  >
                    <div className="w-7 h-7 rounded-lg bg-blue-100 text-blue-700 flex items-center justify-center font-bold">
                      AC
                    </div>
                    <div>
                      <p className="font-semibold">Alex Chen (Student)</p>
                      <p className="text-[10px] text-slate-500">Lost Backpack & ID Card</p>
                    </div>
                  </button>

                  <button
                    onClick={() => {
                      onSwitchUser('staff@campus.edu');
                      setShowUserMenu(false);
                    }}
                    className={`w-full text-left p-2 rounded-xl text-xs flex items-center space-x-2.5 transition-colors ${
                      currentUser?.email === 'staff@campus.edu' ? 'bg-emerald-50 text-emerald-700 font-bold' : 'hover:bg-slate-50 text-slate-700'
                    }`}
                  >
                    <div className="w-7 h-7 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center font-bold">
                      SJ
                    </div>
                    <div>
                      <p className="font-semibold">Sarah Jenkins (Staff)</p>
                      <p className="text-[10px] text-slate-500">Lost & Found Desk / QR Verification</p>
                    </div>
                  </button>

                  <button
                    onClick={() => {
                      onSwitchUser('admin@campus.edu');
                      setShowUserMenu(false);
                    }}
                    className={`w-full text-left p-2 rounded-xl text-xs flex items-center space-x-2.5 transition-colors ${
                      currentUser?.email === 'admin@campus.edu' ? 'bg-purple-50 text-purple-700 font-bold' : 'hover:bg-slate-50 text-slate-700'
                    }`}
                  >
                    <div className="w-7 h-7 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center font-bold">
                      AD
                    </div>
                    <div>
                      <p className="font-semibold">Campus Administrator</p>
                      <p className="text-[10px] text-slate-500">Analytics, Heatmap, Moderation</p>
                    </div>
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};
