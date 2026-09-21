import React, { useState } from 'react';
import {
  Sparkles,
  HelpCircle,
  MapPin,
  Calendar,
  Phone,
  Gift,
  ArrowRight,
  RefreshCw,
  AlertCircle,
  MessageSquare,
  CheckCircle2,
  Image as ImageIcon
} from 'lucide-react';
import { api } from '../services/api';
import { CampusLocation, ItemCategory } from '../types';

interface ReportLostViewProps {
  locations: CampusLocation[];
  categories: ItemCategory[];
  onSuccess: (lostId: number) => void;
  onNavigate: (view: string) => void;
}

const SAMPLE_PROMPTS = [
  "I lost my black Nike backpack with red keychain near Central Library yesterday afternoon.",
  "Misplaced my blue Hydro Flask water bottle around the sports complex basketball court.",
  "Lost my student ID badge with blue lanyard near Academic Block A turnstiles.",
  "Left my brown leather wallet at table 14 in the student cafeteria."
];

export const ReportLostView: React.FC<ReportLostViewProps> = ({
  locations,
  categories,
  onSuccess,
  onNavigate
}) => {
  const [naturalQuery, setNaturalQuery] = useState('');
  const [isProcessingAi, setIsProcessingAi] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Structured form state
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [color, setColor] = useState('');
  const [brand, setBrand] = useState('');
  const [distinctiveFeatures, setDistinctiveFeatures] = useState('');
  const [description, setDescription] = useState('');
  const [locationId, setLocationId] = useState<number>(locations[0]?.id || 1);
  const [specificArea, setSpecificArea] = useState('');
  const [lostDate, setLostDate] = useState<string>(new Date().toISOString().slice(0, 16));
  const [rewardInfo, setRewardInfo] = useState('');
  const [contactPreference, setContactPreference] = useState('In-App Notification');
  const [referenceImageUrl, setReferenceImageUrl] = useState('');

  // Natural Language AI structuring
  const handleAiAssist = async (textToProcess: string) => {
    if (!textToProcess.trim()) return;
    setIsProcessingAi(true);
    setErrorMsg(null);
    try {
      const resp = await api.assistDescription(textToProcess);

      setTitle(resp.brand ? `${resp.color} ${resp.category} (${resp.brand})` : `${resp.color} ${resp.category}`);
      setCategory(resp.category || 'Other');
      setColor(resp.color !== 'Unknown' ? resp.color : '');
      setBrand(resp.brand || '');
      setDescription(resp.structuredDescription || textToProcess);

      if (resp.suggestedLocation) {
        const loc = locations.find(l => l.name.toLowerCase().includes(resp.suggestedLocation.toLowerCase()));
        if (loc) setLocationId(loc.id);
      }
    } catch (err: any) {
      console.error(err);
      setErrorMsg('AI assistant temporarily offline. Please fill fields below.');
    } finally {
      setIsProcessingAi(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !category || !locationId || !description) {
      setErrorMsg('Please ensure title, category, description, and campus location are filled.');
      return;
    }

    setIsSubmitting(true);
    setErrorMsg(null);

    try {
      const payload = {
        title,
        category,
        color,
        brand,
        distinctiveFeatures,
        description,
        locationId,
        specificArea,
        lostDate: new Date(lostDate).toISOString(),
        referenceImageUrl: referenceImageUrl || undefined,
        rewardInfo: rewardInfo || undefined,
        contactPreference
      };

      const result = await api.createLostReport(payload);
      onSuccess(result.id);
      onNavigate('matches');
    } catch (err: any) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to submit lost report');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-10">
      {/* Banner */}
      <div className="text-center max-w-2xl mx-auto mb-10">
        <div className="inline-flex items-center space-x-1.5 bg-amber-50 text-amber-800 px-3 py-1 rounded-full text-xs font-bold mb-3 border border-amber-200">
          <Sparkles className="w-3.5 h-3.5 text-amber-600" />
          <span>Conversational AI Description Assistant</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          Report a Lost Item
        </h1>
        <p className="mt-2 text-sm text-slate-600">
          Describe what you lost in everyday language. Our AI will automatically parse attributes and scan all campus found reports for instant matches.
        </p>
      </div>

      {errorMsg && (
        <div className="mb-6 p-4 rounded-2xl bg-red-50 border border-red-200 flex items-start space-x-3 text-red-800 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600 mt-0.5" />
          <div>
            <p className="font-bold">Error</p>
            <p>{errorMsg}</p>
          </div>
        </div>
      )}

      {/* AI Conversational Assistant Box */}
      <div className="bg-gradient-to-br from-brand-900 to-indigo-950 rounded-3xl p-6 sm:p-8 text-white shadow-xl shadow-brand-900/10 mb-8 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="relative z-10">
          <div className="flex items-center space-x-2 text-cyan-300 text-xs font-bold uppercase tracking-wider mb-2">
            <Sparkles className="w-4 h-4 text-cyan-400" />
            <span>Describe What You're Looking For</span>
          </div>
          <h2 className="text-xl font-bold text-white mb-3">Tell AI What Happened</h2>

          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              value={naturalQuery}
              onChange={(e) => setNaturalQuery(e.target.value)}
              placeholder="e.g. I lost my black Nike backpack with a red keychain near the library yesterday..."
              className="flex-1 px-4 py-3 bg-white/10 border border-white/20 rounded-2xl text-white placeholder-white/50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-400 backdrop-blur-md"
            />
            <button
              type="button"
              onClick={() => handleAiAssist(naturalQuery)}
              disabled={isProcessingAi || !naturalQuery.trim()}
              className="px-6 py-3 rounded-2xl bg-cyan-400 hover:bg-cyan-300 text-slate-900 font-bold text-xs uppercase tracking-wider transition-all flex items-center justify-center space-x-2 shadow-md shadow-cyan-400/20 flex-shrink-0"
            >
              {isProcessingAi ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  <span>Structuring...</span>
                </>
              ) : (
                <>
                  <Sparkles className="w-4 h-4" />
                  <span>Structure with AI</span>
                </>
              )}
            </button>
          </div>

          {/* Quick Click Prompts */}
          <div className="mt-4 pt-4 border-t border-white/10">
            <p className="text-[11px] text-white/60 font-semibold mb-2">Or try a sample description:</p>
            <div className="flex flex-wrap gap-2">
              {SAMPLE_PROMPTS.map((prompt, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() => {
                    setNaturalQuery(prompt);
                    handleAiAssist(prompt);
                  }}
                  className="px-3 py-1.5 rounded-xl bg-white/10 hover:bg-white/20 text-white/90 text-xs transition-colors text-left"
                >
                  "{prompt.slice(0, 42)}..."
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Main Report Form */}
      <form onSubmit={handleSubmit} className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-6">
        <div>
          <span className="text-xs font-black text-brand-600 uppercase tracking-widest">Item Details</span>
          <h2 className="text-lg font-bold text-slate-900">Confirm & Add Specific Details</h2>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          {/* Title */}
          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Item Title / Summary *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Black Nike Backpack with Red Zipper Pull"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Category */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Category *
            </label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            >
              <option value="">Select Category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.name}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          {/* Color */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Color
            </label>
            <input
              type="text"
              value={color}
              onChange={(e) => setColor(e.target.value)}
              placeholder="e.g. Black, Navy Blue, Silver"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Brand */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Brand / Manufacturer
            </label>
            <input
              type="text"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
              placeholder="e.g. Nike, Apple, Hydro Flask"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Distinctive Features */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Distinctive Features / Markings
            </label>
            <input
              type="text"
              value={distinctiveFeatures}
              onChange={(e) => setDistinctiveFeatures(e.target.value)}
              placeholder="e.g. Red keychain, scratch on base, stickers"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Description */}
          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Full Description & Circumstances *
            </label>
            <textarea
              rows={3}
              required
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Describe where you were, what was inside, or any details to help campus security identify it."
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Campus Location */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Last Seen Campus Location *
            </label>
            <select
              value={locationId}
              onChange={(e) => setLocationId(Number(e.target.value))}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            >
              {locations.map((loc) => (
                <option key={loc.id} value={loc.id}>
                  {loc.name} ({loc.zone})
                </option>
              ))}
            </select>
          </div>

          {/* Floor / Area */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Specific Floor or Area
            </label>
            <input
              type="text"
              value={specificArea}
              onChange={(e) => setSpecificArea(e.target.value)}
              placeholder="e.g. 2nd Floor Cubicles, Near Row 3"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Lost Date & Time */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Approximate Lost Date & Time *
            </label>
            <input
              type="datetime-local"
              required
              value={lostDate}
              onChange={(e) => setLostDate(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Optional Reward */}
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Optional Reward Note
            </label>
            <input
              type="text"
              value={rewardInfo}
              onChange={(e) => setRewardInfo(e.target.value)}
              placeholder="e.g. Coffee reward or ₹500"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>

          {/* Optional Reference Image URL */}
          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Reference Photo / URL (Optional)
            </label>
            <input
              type="url"
              value={referenceImageUrl}
              onChange={(e) => setReferenceImageUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-brand-500 font-medium"
            />
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center justify-end space-x-4 pt-4 border-t border-slate-100">
          <button
            type="button"
            onClick={() => onNavigate('browse')}
            className="px-6 py-3 rounded-2xl border border-slate-300 text-slate-700 text-sm font-bold hover:bg-slate-100 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-8 py-3.5 rounded-2xl bg-gradient-to-r from-brand-600 to-indigo-600 text-white text-sm font-bold shadow-lg shadow-brand-500/25 hover:shadow-brand-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center space-x-2"
          >
            {isSubmitting ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Searching Matches...</span>
              </>
            ) : (
              <>
                <span>Publish Report & Scan Matches</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
