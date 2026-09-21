import React, { useState } from 'react';
import {
  Sparkles,
  CheckCircle2,
  AlertCircle,
  MapPin,
  Calendar,
  ArrowRight,
  ShieldCheck,
  Check,
  ChevronRight,
  HelpCircle
} from 'lucide-react';
import { MatchCandidate, FoundReport } from '../types';

interface MatchCenterViewProps {
  matches: MatchCandidate[];
  onClaimItem: (foundReportId: number, lostReportId?: number) => void;
  onNavigate: (view: string) => void;
}

export const MatchCenterView: React.FC<MatchCenterViewProps> = ({
  matches,
  onClaimItem,
  onNavigate
}) => {
  const [selectedMatchIndex, setSelectedMatchIndex] = useState<number>(0);
  const activeMatch = matches[selectedMatchIndex] || matches[0];

  if (matches.length === 0) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <div className="w-16 h-16 bg-brand-50 text-brand-600 rounded-3xl flex items-center justify-center mx-auto mb-4">
          <Sparkles className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-black text-slate-900">No Potential Matches Discovered Yet</h2>
        <p className="text-sm text-slate-600 mt-2 max-w-md mx-auto">
          Our AI matching engine scans all campus found reports against your lost items continuously. When a report matches, you will receive an alert.
        </p>
        <div className="mt-8 flex justify-center gap-4">
          <button
            onClick={() => onNavigate('report-lost')}
            className="px-6 py-3 rounded-2xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-md transition-colors"
          >
            File a Lost Item Report
          </button>
          <button
            onClick={() => onNavigate('browse')}
            className="px-6 py-3 rounded-2xl bg-white border border-slate-300 text-slate-700 font-bold text-xs hover:bg-slate-50 transition-colors"
          >
            Browse All Found Items
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="text-center max-w-2xl mx-auto mb-10">
        <div className="inline-flex items-center space-x-1.5 bg-indigo-50 text-indigo-700 px-3 py-1 rounded-full text-xs font-bold mb-3 border border-indigo-200">
          <Sparkles className="w-3.5 h-3.5 text-indigo-600" />
          <span>Explainable Multi-Signal Semantic Matching</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          Campus Match Center
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Compare candidate found items against your lost reports. Review individual signal weights and verify ownership.
        </p>
      </div>

      {/* Match Selector Tabs if multiple */}
      {matches.length > 1 && (
        <div className="flex items-center space-x-3 overflow-x-auto pb-4 mb-6">
          {matches.map((m, idx) => (
            <button
              key={m.id}
              onClick={() => setSelectedMatchIndex(idx)}
              className={`p-3 rounded-2xl border text-left transition-all min-w-[240px] flex-shrink-0 ${
                selectedMatchIndex === idx
                  ? 'border-indigo-600 bg-indigo-50/60 shadow-sm'
                  : 'border-slate-200 bg-white hover:border-slate-300'
              }`}
            >
              <div className="flex items-center justify-between mb-1">
                <span className="text-[10px] font-mono text-slate-500 font-bold">
                  {m.lostReferenceId} ↔ {m.foundReferenceId}
                </span>
                <span className={`text-xs font-black px-2 py-0.5 rounded-full ${
                  m.overallScore >= 80 ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'
                }`}>
                  {m.overallScore}%
                </span>
              </div>
              <p className="text-xs font-bold text-slate-800 truncate">{m.lostTitle}</p>
            </button>
          ))}
        </div>
      )}

      {/* Main Side-by-Side Comparison Container */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden mb-8">
        {/* Top Match Score Bar */}
        <div className="p-6 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-emerald-500 to-teal-400 text-slate-950 flex flex-col items-center justify-center font-black shadow-lg shadow-emerald-500/20 flex-shrink-0">
              <span className="text-xl leading-none">{activeMatch.overallScore}%</span>
              <span className="text-[9px] uppercase tracking-wider font-bold">Match</span>
            </div>
            <div>
              <div className="inline-flex items-center space-x-1.5 text-xs text-emerald-400 font-bold">
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>High Correlation Detected by CampusFind AI</span>
              </div>
              <h2 className="text-lg font-bold text-white mt-0.5">
                Potential Match Found
              </h2>
              <p className="text-xs text-slate-300">
                AI analyzed 6 signals: Category, Color, Brand, Location, Date proximity, and Description tokens.
              </p>
            </div>
          </div>

          <button
            onClick={() => onClaimItem(activeMatch.foundReportId, activeMatch.lostReportId)}
            className="w-full sm:w-auto px-6 py-3 rounded-2xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-xs uppercase tracking-wider shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center space-x-2"
          >
            <span>Claim This Item</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {/* Side-by-Side Cards */}
        <div className="p-6 sm:p-8 grid grid-cols-1 lg:grid-cols-2 gap-8 border-b border-slate-100">
          {/* LEFT: Your Lost Report */}
          <div className="p-5 rounded-2xl bg-amber-50/40 border border-amber-200/80">
            <div className="flex items-center justify-between mb-4">
              <span className="px-3 py-1 rounded-full text-xs font-black bg-amber-100 text-amber-900">
                YOUR LOST REPORT
              </span>
              <span className="text-xs font-mono font-bold text-amber-800">
                {activeMatch.lostReferenceId}
              </span>
            </div>

            {activeMatch.lostImageUrl && (
              <img
                src={activeMatch.lostImageUrl}
                alt="Lost Item Reference"
                className="w-full h-48 object-cover rounded-xl mb-4 border border-amber-200"
              />
            )}

            <h3 className="font-bold text-slate-900 text-lg">{activeMatch.lostTitle}</h3>

            <div className="mt-4 space-y-2 text-xs">
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Category</span>
                <span className="font-bold text-slate-800">{activeMatch.lostCategory}</span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Last Seen Location</span>
                <span className="font-bold text-slate-800 flex items-center space-x-1">
                  <MapPin className="w-3.5 h-3.5 text-amber-600" />
                  <span>{activeMatch.lostLocation}</span>
                </span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-amber-100">
                <span className="text-slate-500 font-semibold">Reported Lost Date</span>
                <span className="font-bold text-slate-800">
                  {new Date(activeMatch.lostDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>

          {/* RIGHT: Candidate Found Report */}
          <div className="p-5 rounded-2xl bg-emerald-50/40 border border-emerald-200/80">
            <div className="flex items-center justify-between mb-4">
              <span className="px-3 py-1 rounded-full text-xs font-black bg-emerald-100 text-emerald-900">
                CANDIDATE FOUND REPORT
              </span>
              <span className="text-xs font-mono font-bold text-emerald-800">
                {activeMatch.foundReferenceId}
              </span>
            </div>

            {activeMatch.foundImageUrl && (
              <img
                src={activeMatch.foundImageUrl}
                alt="Found Item Photo"
                className="w-full h-48 object-cover rounded-xl mb-4 border border-emerald-200"
              />
            )}

            <h3 className="font-bold text-slate-900 text-lg">{activeMatch.foundTitle}</h3>

            <div className="mt-4 space-y-2 text-xs">
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Category</span>
                <span className="font-bold text-slate-800">{activeMatch.foundCategory}</span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Found Location</span>
                <span className="font-bold text-slate-800 flex items-center space-x-1">
                  <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{activeMatch.foundLocation}</span>
                </span>
              </div>
              <div className="flex items-center justify-between py-1.5 border-b border-emerald-100">
                <span className="text-slate-500 font-semibold">Found Date</span>
                <span className="font-bold text-slate-800">
                  {new Date(activeMatch.foundDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Explainable Signals Checklist */}
        <div className="p-6 sm:p-8 bg-slate-50/60">
          <h3 className="text-sm font-bold text-slate-900 mb-1 flex items-center space-x-2">
            <ShieldCheck className="w-4 h-4 text-indigo-600" />
            <span>Why We Think These Reports May Match</span>
          </h3>
          <p className="text-xs text-slate-500 mb-4">
            The multi-signal matching engine computed the following compatibility factors:
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
            {activeMatch.matchReasons.map((reason, i) => (
              <div
                key={i}
                className="p-3 bg-white rounded-xl border border-slate-200 shadow-sm flex items-center space-x-2.5"
              >
                <div className="w-5 h-5 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center flex-shrink-0">
                  <Check className="w-3.5 h-3.5" />
                </div>
                <span className="text-xs font-semibold text-slate-800">{reason.replace(/^[✓~]\s*/, '')}</span>
              </div>
            ))}
          </div>

          {/* Action CTA */}
          <div className="mt-8 pt-6 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="text-xs text-slate-500 flex items-center space-x-1.5">
              <HelpCircle className="w-4 h-4 text-slate-400" />
              <span>Does this look like your item? Click below to begin blind verification.</span>
            </div>
            <button
              onClick={() => onClaimItem(activeMatch.foundReportId, activeMatch.lostReportId)}
              className="px-8 py-3.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white font-bold text-sm shadow-md transition-all flex items-center space-x-2"
            >
              <span>Verify & Claim Item</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
